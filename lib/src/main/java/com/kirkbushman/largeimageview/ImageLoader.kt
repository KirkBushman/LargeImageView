package com.kirkbushman.largeimageview

import android.content.Context
import android.view.View

interface ImageLoader {

    /**
     * Receive from the user the view that binds to thumbnailView
     */
    fun getThumbnailView(context: Context): View?

    /**
     * Load the image to the View provided, the method is up to the user
     */
    fun loadThumbnail(view: View)

    /**
     * Receive from the user the view that binds to errorView
     */
    fun getErrorView(context: Context): View?

    /**
     * Start preloading the large image as a java File,
     * the method used is up to the user, use the callback to report if the image is available
     * or the fetching went wrong.
     */
    fun preloadSource(callback: ImageReadyCallback)
}
