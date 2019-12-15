package com.kirkbushman.largeimageview.sampleapp.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

fun Context.showToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
}

fun RequestManager.loadThumbnail(url: String, imageView: ImageView) {

    val options = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .override(300, 300)

    asBitmap()
        .load(url)
        .apply(options)
        .into(imageView)
}
