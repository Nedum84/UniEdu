package com.uniedu.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import androidx.annotation.RawRes
import com.uniedu.utils.ClassUtilities
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


// ...
private var mPdfRenderer: PdfRenderer? = null
private var mPdfPage: PdfRenderer.Page? = null

//override fun onDestroy() {
//    super.onDestroy()
//    if (mPdfPage != null) {
//        mPdfPage?.close()
//    }
//    if (mPdfRenderer != null) {
//        mPdfRenderer?.close()
//    }
//}
@Throws(IOException::class)
fun copyToLocalCache(outputFile: File, @RawRes pdfResource: Int, context: Context) {
    if (!outputFile.exists()) {
        val input: InputStream = context.resources.openRawResource(pdfResource)
        val output = FileOutputStream(outputFile)
        val buffer = ByteArray(1024)
        var size: Int
        // Just copy the entire contents of the file
        while (input.read(buffer).also { size = it } != -1) {
            output.write(buffer, 0, size)
        }
        input.close()
        output.close()
    }
}

// Display a page from the PDF on an ImageView
@Throws(IOException::class)
fun String?.openPdfWithAndroidSDK(pageNumber: Int=0):Bitmap {
    // Copy sample.pdf from 'res/raw' folder into local cache so PdfRenderer can handle it
    val fileCopy = File(this!!)

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
//        mPdfPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

    // Set the bitmap in the ImageView so we can view it
    try {
        mPdfRenderer?.close()
        mPdfPage?.close()
    } catch (e: Exception) {  e.printStackTrace()  }

    return bitmap
}

fun Context.saveImgFromBitmap(bitmap: Bitmap):String{
    var pdfImg = ""
    val dirPath = ClassUtilities().getDirPath(this, "tmp")
    val fileName = ClassUtilities().assignImgName()
    val path = "$dirPath/$fileName"

    val file = File(path)
//        val file = File(dirPath, fileName)
    try {
        if (!file.exists()) {
            file.createNewFile()
        }
        val ostream = FileOutputStream(file)
//            bitmap.compress(CompressFormat.PNG, 10, ostream)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream)
        ostream.close()
        pdfImg = path
    } catch (e: Exception) {
        e.printStackTrace()
        pdfImg = ""
    }

    return pdfImg
}