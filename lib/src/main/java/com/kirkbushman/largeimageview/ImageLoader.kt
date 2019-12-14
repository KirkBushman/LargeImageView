package com.kirkbushman.largeimageview

import android.content.Context
import android.view.View

interface ImageLoader {

    fun getThumbnailView(context: Context): View
    fun loadThumbnail(view: View, url: String)

    fun preloadSource(url: String)

    // fun onImageDownloaded(onLoaded: () -> Unit)
    // fun onImageErrored(onErrored: () -> Unit)
}
