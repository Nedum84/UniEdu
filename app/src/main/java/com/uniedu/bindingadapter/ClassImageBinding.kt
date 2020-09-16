package com.uniedu.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.uniedu.R

object ClassImageBinding {

    val sampleImage = R.drawable.test
    //For Image view Adapter
    @JvmStatic // add this line !!
    @BindingAdapter("imagePath")
    fun bindImageFromUrl(view: ImageView, imgUrlString: String){
        if (imgUrlString.isNotEmpty()){
            Glide.with(view.context)
                .load(imgUrlString)
                .into(view)
        }
    }

    //For Int Images...
    @JvmStatic // add this line !!
    @BindingAdapter("imagePath")
    fun bindImageFromUrl(view: ImageView, imgUrlString: Int){
        Glide.with(view.context)
            .load(imgUrlString)
            .into(view)
    }


    //For Profile Images
    @JvmStatic // add this line !!
    @BindingAdapter("imagePathPhoto")
    fun bindImageFromUrlPhoto(view: ImageView, imgUrlString: String){
        if (imgUrlString.isNotEmpty()){
            Glide.with(view.context)
                .load(imgUrlString)
                .into(view)
        }
    }
}