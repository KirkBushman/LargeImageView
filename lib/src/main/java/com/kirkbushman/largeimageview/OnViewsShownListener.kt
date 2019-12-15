package com.kirkbushman.largeimageview

import android.view.View
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

interface OnViewsShownListener {

    /**
     * Called after the thumbnail view is attached as children and shown
     */
    fun onThumbnailViewShown(view: View)

    /**
     * Called after the image view is attached as children and shown
     */
    fun onImageViewShown(view: SubsamplingScaleImageView)

    /**
     * Called after the error view is attached as children and shown
     */
    fun onErrorViewShown(view: View)
}
