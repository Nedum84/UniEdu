package com.uniedu.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_crop_image.*
import android.graphics.RectF
import com.isseiaoki.simplecropview.callback.LoadCallback
import android.graphics.Bitmap
import android.net.Uri
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.SaveCallback
import java.io.File
import android.provider.MediaStore
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import com.isseiaoki.simplecropview.CropImageView
import com.uniedu.R
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.utils.ClassSharedPreferences
import com.uniedu.utils.ClassUtilities


class ActivityCropImage : AppCompatActivity(), View.OnClickListener {

    private val mFrameRect: RectF? = null
    private val mCompressFormat = Bitmap.CompressFormat.JPEG
    lateinit var mSourceUri:Uri
    lateinit var progresDialog: ClassProgressDialog
    lateinit var outputFilePath:String

    lateinit var thisContext: Context
    lateinit var prefs: ClassSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_image)
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        thisContext = this
        prefs = ClassSharedPreferences(thisContext)
        progresDialog = ClassProgressDialog(
            thisContext,"Saving, Please Wait..."
        )
        if(prefs.getImgUploadPath() !=""){
            mSourceUri = Uri.fromFile(File(prefs.getImgUploadPath()))
        }else{
            super.onBackPressed()
        }

//        mSourceUri = Uri.fromFile(File("/sdcard/sample.jpg"))
        bindViews()
        // load image
        mCropView.load(mSourceUri)
            .initialFrameRect(mFrameRect)
            .useThumbnail(true)
            .execute(mLoadCallback)

    }
    private fun cropImage(){
        progresDialog.createDialog()
        mCropView.crop(mSourceUri).execute(object : CropCallback {
            override fun onSuccess(cropped: Bitmap) {
                mCropView.setCompressFormat(mCompressFormat)
                mCropView.setCompressQuality(75)//added by me
                mCropView.save(cropped).execute(createSaveUri(), mSaveCallback)
            }

            override fun onError(e: Throwable) {}
        })

    }
    private val mLoadCallback = object : LoadCallback {
        override fun onSuccess() {
            Toast.makeText(thisContext, "Crop your image...", Toast.LENGTH_SHORT).show()
        }

        override fun onError(e: Throwable) {
            Toast.makeText(thisContext, "Error occurred, try again...", Toast.LENGTH_SHORT).show()
        }
    }

    private val mSaveCallback = object : SaveCallback {
        override fun onSuccess(uri: Uri?) {
            progresDialog.dismissDialog()
            prefs.setImgUploadPath(outputFilePath)
            backPressed()
            finish()
        }

        override fun onError(e: Throwable?) {
            backPressed()
            progresDialog.dismissDialog()
        }
    }
    fun backPressed(){
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#1C1C1C")
        }
    }
    override fun finish() {
        super.finish()
        prefs.isImageUnderCropping(false)
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(thisContext, R.color.colorPrimaryDark)
        }
    }
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonDone -> cropImage()
            R.id.buttonFitImage -> mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE)
            R.id.button1_1 -> mCropView.setCropMode(CropImageView.CropMode.SQUARE)
            R.id.button3_4 -> mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4)
            R.id.button4_3 -> mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3)
            R.id.button9_16 -> mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16)
            R.id.button16_9 -> mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9)
            R.id.buttonCustom -> mCropView.setCustomRatio(7, 5)
            R.id.buttonFree -> mCropView.setCropMode(CropImageView.CropMode.FREE)
            R.id.buttonCircle -> mCropView.setCropMode(CropImageView.CropMode.CIRCLE)
            R.id.buttonShowCircleButCropAsSquare -> mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE)
            R.id.buttonRotateLeft -> mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D)
            R.id.buttonRotateRight -> mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D)
            R.id.buttonScanTextFromImage -> super.onBackPressed()
            else ->{
                super.onBackPressed()
            }
        }
    }

    private fun bindViews() {
        buttonDone.setOnClickListener(this)
        buttonFitImage.setOnClickListener(this)
        button1_1.setOnClickListener(this)
        button3_4.setOnClickListener(this)
        button4_3.setOnClickListener(this)
        button9_16.setOnClickListener(this)
        button16_9.setOnClickListener(this)
        buttonFree.setOnClickListener(this)
        buttonScanTextFromImage.setOnClickListener(this)
        buttonRotateLeft.setOnClickListener(this)
        buttonRotateRight.setOnClickListener(this)
        buttonCustom.setOnClickListener(this)
        buttonCircle.setOnClickListener(this)
        buttonShowCircleButCropAsSquare.setOnClickListener(this)
    }

    fun createSaveUri(): Uri {
        return createNewUri(this, mCompressFormat)!!
    }

    private fun createNewUri(context: Context, format: Bitmap.CompressFormat): Uri? {
        val currentTimeMillis = System.currentTimeMillis()
        val today = Date(currentTimeMillis)
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val title = dateFormat.format(today)
        val dirPath = ClassUtilities().getDirPath(thisContext, "tmp")
        val fileName = ClassUtilities().assignImgName()
        val path = "$dirPath/$fileName"
        outputFilePath = path
        val file = File(path)
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format))
        values.put(MediaStore.Images.Media.DATA, path)
        val time = currentTimeMillis / 1000
        values.put(MediaStore.MediaColumns.DATE_ADDED, time)
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time)
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length())
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        Log.d("SaveUri =", uri!!.toString())
        return uri
    }

    private fun getMimeType(format: Bitmap.CompressFormat): String {
//        Logger.i("getMimeType CompressFormat = $format")
        when (format) {
            Bitmap.CompressFormat.JPEG -> return "jpeg"
            Bitmap.CompressFormat.PNG -> return "png"
            else -> {

            }
        }
        return "png"
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_activity_crop_image, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
            R.id.menu_action_go_back ->{
                super.onBackPressed()
            }
            R.id.menu_action_save_cropped_img2 ->{
                cropImage()
            }
            R.id.menu_action_save_cropped_img ->{
                cropImage()
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

}
