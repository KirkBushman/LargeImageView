package com.kirkbushman.largeimageview.sampleapp.utils

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object Utils {

    private const val ERR_MSG = "Something went wrong!"

    fun getLoadingView(context: Context): View {

        return ProgressBar(context)
    }

    fun getErrorView(context: Context): View {
        val errorText = TextView(context)
        errorText.text = ERR_MSG
        errorText.typeface = Typeface.DEFAULT_BOLD
        errorText.textSize = 21f
        errorText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        errorText.gravity = Gravity.CENTER

        return errorText
    }
}

fun Context.showToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
}

fun RequestManager.loadThumbnail(url: String, imageView: ImageView) {

    val options = RequestOptions()
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .override(300, 300)

    asBitmap()
        .load(url)
        .apply(options)
        .into(imageView)
}
