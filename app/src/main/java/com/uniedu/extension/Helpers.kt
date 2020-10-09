package com.uniedu.extension

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uniedu.model.Questions

fun Context.toast(message:String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun EditText.toString()  = this.toString().trim()


private val imgSrcRgx = "src=\"(.*?)\"".toRegex(RegexOption.IGNORE_CASE)
fun String.getImgPaths() = (imgSrcRgx.findAll(this).map {it.value.replace("src=","").replace("\"","")}.toList())
    .filter { !it.contains("http") }//excluding http(server) images

fun String.getImgPathsWithHttp() = (imgSrcRgx.findAll(this).map {it.value.replace("src=","").replace("\"","")}.toList())
    //Including server images

private val imgTagRgx = "/<img[^>]*>/g".toRegex(RegexOption.IGNORE_CASE)
fun String.removeImgTags() = this.replace(imgTagRgx,"")


fun <T: Any> String.toListModel(item:T):List<T>{
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(MutableList::class.java, item::class.java)
    val adapter: JsonAdapter<List<T>> = moshi.adapter(type)
    val modelList: List<T> = adapter.fromJson(this)!!

    return modelList

//    val cls = (t as Any).javaClass //unsafe
//    val clsOrNull = (t as? Any)?.javaClass //safe

}
inline fun <reified T : Any> test(t: T) {
    println(T::class)
}

fun List<String>.replaceWithNewImgPath(htmlString: String, serverResponse:String?):String{
    var content = htmlString

    val eachImg = serverResponse?.split(",,,")
    var idx = 0
    eachImg?.forEach {
        val bothUrl = it.trim().split("===")
        if (bothUrl.size>1){
            val  newUrl = bothUrl[1]
            content = content.replace(this[idx], "\'$newUrl\'")
        }
        idx++
    }

    return content
}

//Click listener to constraint lsyout group 1
fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}
//Click listener to constraint layout group 2
fun Group.addOnClickListener(listener: (view: View) -> Unit) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}
//get extension
fun String.getFileExt():String{
    return this.split(".").last().toLowerCase()
}