package com.uniedu.extension

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uniedu.model.Questions

fun Context.toast(message:String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun EditText.toString(input:EditText?)  = input.toString().trim()


private val imgSrcRgx = "src=\"(.*?)\"".toRegex(RegexOption.IGNORE_CASE)
fun String.getImgPaths() = (imgSrcRgx.findAll(this).map {it.value.replace("src=","").replace("\"","")}.toList())
                            .filter { !it.contains("http") }

private val imgTagRgx = "/<img[^>]*>/g".toRegex(RegexOption.IGNORE_CASE)
fun String.removeImgTags() = this.replace(imgTagRgx,"")


fun <T: Any> String.toListModel(item:T):List<T>{
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(MutableList::class.java, item::class.java)
    val adapter: JsonAdapter<List<T>> = moshi.adapter(type)
    val modelList: List<T> = adapter.fromJson(this)!!

    return modelList
}

fun List<String>.replaceWithNewImgPath(htmlString: String, serverResponse:String?):String{
    var content = htmlString

    val eachImg = serverResponse?.split(",,,")
    var idx = 0;
    eachImg?.forEach {
        val bothUrl = it.trim().split("===")
        if (bothUrl.size>1){
//            val oldPath = this.filter { it.contains(bothUrl[0], true) }
            val  newUrl = bothUrl[1]
            content = content.replace(this[idx], newUrl)
        }
        idx++
    }

    return content
}