package com.kirkbushman.largeimageview

import java.io.File

interface ImageReadyCallback {

    /**
     * The large image preloaded is ready, as a java File Object
     */
    fun onImageReady(file: File)

    /**
     * The process did return an error.
     */
    fun onImageErrored()
}
