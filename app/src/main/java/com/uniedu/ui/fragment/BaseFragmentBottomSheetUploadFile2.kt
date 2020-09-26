package com.uniedu.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.uniedu.BuildConfig
import com.uniedu.R
import com.uniedu.extension.*
import com.uniedu.ui.activity.ActivityCropImage
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassUtilities
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.alert_dialog_inflate_preview_image.view.*
import kotlinx.android.synthetic.main.bottomsheet_ebook_upload_type.view.*
import kotlinx.android.synthetic.main.fragment_upload_e_book.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

abstract class BaseFragmentBottomSheetUploadFile2 : BaseFragmentBottomSheet(){

    var fileUri: Uri? = null
    var mediaPath: String? = null
    var mImageFileLocation = ""
    var imageFilePath: String? = null

    var fileType = "IMAGE"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //image preview
        imagePreviewWrapper.setOnClickListener {
            showImageDialog()
        }
        removeImageBTN.setOnClickListener {
            removeImage()
        }
        // get the file url
        fileUri = savedInstanceState?.getParcelable("file_uri")
    }


    fun bottomSheetDialog() {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED||
            (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                thisContext,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                0
            )
        }//else return


//        val bottomSheetDialogView: View = layoutInflater.inflate(R.layout.bottomsheet_ebook_upload_type, null)
        val bottomSheetDialogView: View =LayoutInflater.from(context).inflate(R.layout.bottomsheet_ebook_upload_type, null)
        val bSheetDialog = BottomSheetDialog(requireContext())
        bSheetDialog.setContentView(bottomSheetDialogView)
        val bSheetBehaviour = bSheetDialog.behavior
        bSheetDialog.show()

        bottomSheetDialogView.pdf_upload.setAllOnClickListener(View.OnClickListener {
            bSheetDialog.dismiss()

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "application/pdf"
//            startActivity(intent)
            startActivityForResult(intent, REQUEST_PICK_PHOTO)
        })
        bottomSheetDialogView.gallery_upload.addOnClickListener {
            bSheetDialog.dismiss()
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO)
        }
        bottomSheetDialogView.camera_upload.addOnClickListener {
            bSheetDialog.dismiss()
            captureImage()
        }

        bSheetBehaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> ""
                    BottomSheetBehavior.STATE_COLLAPSED -> ""
                    else -> ""
                }
            }
        })
    }

    private fun showImageDialog(){
        ClassUtilities().hideKeyboard(view, thisContext)
        val inflater = LayoutInflater.from(context).inflate(R.layout.alert_dialog_inflate_preview_image, null)
        val builder = AlertDialog.Builder(thisContext)

        if(imageFilePath!=null){
            Glide.with(this)
                .load(
                    if(fileType=="pdf") pdfImgPath else imageFilePath
                )
                .into(inflater.dialogPreviewImage)
        }

        builder.setView(inflater)
        if (fileType !="pdf"){
            builder.setPositiveButton("Crop"
            ) { _, _ ->
                //actions
                prefs.setImgUploadPath(imageFilePath!!)
                gotoCropActivity()
            }
        }
        builder.setNeutralButton("Remove"
        ) { _, _ ->
            //actions
            removeImage()
        }

        val alertDialog = builder.create()
        alertDialog.show()

    }


    override fun onResume() {
        super.onResume()
        if(prefs.getImgUploadPath() !="") {
            imageFilePath = prefs.getImgUploadPath()

            val postPathFile = File(imageFilePath!!)
            val inputFileInKb = postPathFile.length()/1024//KB like 358BB
            if((imageFilePath == null)||(postPathFile.length() <=3)){
                thisContext.toast("No Image selected...")
                removeImage()
                return
            }else if((inputFileInKb >= 1024)){//more than or equals 1MB
                imageFilePath = compressAndAssignPath(postPathFile)//resizing image...
            }

            imagePreviewWrapper.visibility = View.VISIBLE
            pickImage.visibility = View.GONE
            Glide.with(this).load(imageFilePath).into(imagePreview)

        }else if(fileType!="pdf"){
            removeImage()
        }

    }
    private fun removeImage(){
        imagePreview?.setImageDrawable(null)
        imagePreviewWrapper?.visibility = View.GONE

        pickImage.visibility = View.VISIBLE
        imageFilePath = null
        prefs.setImgUploadPath("")
    }

    private fun compressAndAssignPath(postPathFile:File) :String?{
        val tmpImgPath = ClassUtilities().getDirPath(thisContext, "tmp")

        val filePath = "$tmpImgPath/${imageFilePath!!.split("/").last()}"
        try {
            Compressor(thisContext)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(tmpImgPath)
                .compressToFile(postPathFile)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return filePath
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            imagePreviewWrapper.visibility = View.VISIBLE

            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    // Get the Image from data
                    val selectedImage = data.data!!
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                    val cursor = requireActivity().contentResolver.query(selectedImage, filePathColumn, null, null, null)!!
                    cursor.moveToFirst()

                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
//                    mediaPath = cursor.getString(columnIndex)
//                    OR
                    mediaPath = ClassUtilities().getFilePathFromURI(thisContext, selectedImage)
                    // Set the Image in ImageView for Previewing the Media
                    val options = BitmapFactory.Options()//additional parameter
                    options.inSampleSize = 2//additional parameter

                    imagePreview.setImageBitmap(BitmapFactory.decodeFile(mediaPath, options))
                    cursor.close()


                    imageFilePath = mediaPath
                }

            } else if (requestCode == CAMERA_PIC_REQUEST) {
                if (Build.VERSION.SDK_INT <= 21) {
                    Glide.with(this).load(fileUri).into(imagePreview)
                    imageFilePath = fileUri!!.path

                } else {

                    Glide.with(this).load(mImageFileLocation).into(imagePreview)
                    imageFilePath = mImageFileLocation

                }

            }







            if (imageFilePath!!.getFileExt() != "pdf"){
                fileType = "image"
                resizeAndGotoCrop()
            }else{
                fileType = "pdf"
                pickImage.visibility = View.GONE
                val imgBitmap = openPdfWithAndroidSDK()
                pdfImgPath = context?.saveImgFromBitmap(imgBitmap)!!

                imagePreview.setImageBitmap(imgBitmap)
            }

        } else if (resultCode != Activity.RESULT_CANCELED) {
            Toast.makeText(thisContext, "Sorry, there was an error!", Toast.LENGTH_LONG).show()
        }
    }
    private var pdfImgPath = ""
    private var mPdfRenderer: PdfRenderer? = null
    private var mPdfPage: PdfRenderer.Page? = null

    override fun onDestroy() {
        super.onDestroy()
        if (mPdfPage != null) {
            mPdfPage?.close()
        }
        if (mPdfRenderer != null) {
            mPdfRenderer?.close()
        }
    }
    // Display a page from the PDF on an ImageView
    @Throws(IOException::class)
    fun openPdfWithAndroidSDK(pageNumber: Int=0):Bitmap {
        // Copy sample.pdf from 'res/raw' folder into local cache so PdfRenderer can handle it
        val fileCopy = File(imageFilePath!!)

        // We will get a page from the PDF file by calling openPage
        val fileDescriptor: ParcelFileDescriptor = ParcelFileDescriptor.open(
            fileCopy,
            ParcelFileDescriptor.MODE_READ_ONLY
        )
        mPdfRenderer = PdfRenderer(fileDescriptor)
        mPdfPage = mPdfRenderer?.openPage(pageNumber)

        // Create a new bitmap and render the page contents on to it
        val bitmap = Bitmap.createBitmap(
            mPdfPage!!.width,
            mPdfPage!!.height,
            Bitmap.Config.ARGB_8888
        )
//        For pdf rendering
        mPdfPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        return bitmap
    }


    private fun resizeAndGotoCrop() {
        val postPathFile = File(imageFilePath!!)
        val inputFileInKb = postPathFile.length()/1024//KB like 358BB
        if((imageFilePath == null)||(postPathFile.length() <=3)){
            ClassAlertDialog(thisContext).toast("No Image selected...")
            prefs.setImgUploadPath("")
            return
        }else if((inputFileInKb >= 500)){//more than or equals 500KB
            imageFilePath = compressAndAssignPath(postPathFile)//resizing image...
        }


        prefs.setImgUploadPath(imageFilePath!!)
        gotoCropActivity()

        pickImage.visibility = View.GONE
    }
    private fun gotoCropActivity(){

        startActivity(Intent(thisContext, ActivityCropImage::class.java))
        thisContext.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
    }

    /**
     * Launching camera app to capture image
     */

    @Throws(IOException::class)
    internal fun createImageFile(): File {
        Logger.getAnonymousLogger().info("Generating the image - method started")

        // Here we create a "non-collision file name", alternatively said, "an unique filename" using the "timeStamp" functionality
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.getDefault()).format(Date())
        val imageFileName = "IMAGE_" + timeStamp
        // Here we specify the environment location and the exact path where we want to save the so-created file
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/.photo_saving_app_lol")
//        val storageDirectory = File(ClassUtilities().getDirPath(thisContext, "tmp"))
        Logger.getAnonymousLogger().info("Storage directory set")

        // Then we create the storage directory if does not exists
        if (!storageDirectory.exists()) storageDirectory.mkdir()

        // Here we create the file using a prefix, a suffix and a directory
        val image = File(storageDirectory, "$imageFileName.png")
//        val image = File(createNewImageUri()!!.path!!)
        // File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Here the location is saved into the string mImageFileLocation
        Logger.getAnonymousLogger().info("File name and path set")

        mImageFileLocation = image.absolutePath
        // fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image
    }

    private fun getOutputMediaFileUri(type: Int): Uri {
        // Create a media file name
        val tmpImgPath = ClassUtilities().getDirPath(thisContext, "tmp")
        val mediaFile = File(tmpImgPath + File.separator+ ClassUtilities().assignImgName(false) + ".jpg")
        return Uri.fromFile(mediaFile)
    }
    private fun captureImage() {
        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            val callCameraApplicationIntent = Intent()
            callCameraApplicationIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

            // We give some instruction to the intent to save the image
            var photoFile: File? = null

            try {
                // If the createImageFile will be successful, the photo file will have the address of the file
                photoFile = createImageFile()
                // Here we call the function that will try to catch the exception made by the throw function
            } catch (e: IOException) {
                Logger.getAnonymousLogger().info("Exception error in generating the file")
                e.printStackTrace()
            }

            // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
            val outputUri = FileProvider.getUriForFile(
                thisContext,BuildConfig.APPLICATION_ID + ".provider",photoFile!!)
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)

            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            Logger.getAnonymousLogger().info("Calling the camera App by intent")

            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, CAMERA_PIC_REQUEST)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

            startActivityForResult(intent, CAMERA_PIC_REQUEST)
        }


    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ClassUtilities().createDirs(thisContext)
                bottomSheetDialog()
            }else{
                ClassAlertDialog(thisContext).alertMessage("You must allow camera permission to proceed")
            }
        }
    }



    companion object {
        private const val REQUEST_TAKE_PHOTO = 0
        private const val REQUEST_PICK_PHOTO = 2
        private const val CAMERA_PIC_REQUEST = 1111

        private val TAG = this::class.java.simpleName

        private const val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100

        const val MEDIA_TYPE_IMAGE = 1
        const val IMAGE_DIRECTORY_NAME = "Android File Upload"

        /**
         * returning image / video
         */
        private fun getOutputMediaFile(type: Int): File? {

            // External sdcard location
            val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME)

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "Oops! File Failed to create $IMAGE_DIRECTORY_NAME directory")
                    return null
                }
            }

            // Create a media file name
            //val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val mediaFile: File
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = File(mediaStorageDir.path + File.separator
                        + "IMG_" + ".jpg")
            } else {
                return null
            }

            return mediaFile
        }
    }

}