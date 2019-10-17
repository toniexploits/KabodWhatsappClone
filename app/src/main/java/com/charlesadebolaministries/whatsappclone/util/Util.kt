package com.charlesadebolaministries.whatsappclone.util

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.charlesadebolaministries.whatsappclone.R
import java.text.DateFormat
import java.util.*

fun populateImage(context: Context?, url: String?, imageView: ImageView, errorDrawable: Int = R.drawable.empty ){
    if(context != null) {
        val options = RequestOptions()
            .placeholder(progressDrawable(context))
            .error(errorDrawable)

        Glide.with(context)
            .load(url)
            .apply(options)
            .into(imageView)
    }
}

fun progressDrawable(context: Context): CircularProgressDrawable{
    return CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
}

fun getTime(): String{
    val df = DateFormat.getInstance()
    return df.format(Date())
}