package com.uniedu.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uniedu.R

object ClassImageBinding {

    //For Image view Adapter
    @JvmStatic // add this line !!
    @BindingAdapter("imagePath")
    fun bindImageFromUrl(view: ImageView, imgUrlString: String?  = ""){
        if (!imgUrlString.isNullOrEmpty()){
            Glide.with(view.context)
                .load(imgUrlString)
                .apply(
                    RequestOptions()
//                        .placeholder(R.drawable.test)//default image on loading
                        .error(R.drawable.test)//without n/w, this img shows
//                        .dontAnimate()
//                        .fitCenter()
                )
                .thumbnail(.1f)
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