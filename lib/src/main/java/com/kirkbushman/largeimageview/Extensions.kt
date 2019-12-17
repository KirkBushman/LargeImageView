package com.kirkbushman.largeimageview

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.EASE_OUT_QUAD

fun SubsamplingScaleImageView.scaleMax(duration: Long, listener: SubsamplingScaleImageView.OnAnimationEventListener? = null) {

    animateToScale(maxScale, duration, listener)
}

fun SubsamplingScaleImageView.scaleMin(duration: Long, listener: SubsamplingScaleImageView.OnAnimationEventListener? = null) {

    animateToScale(minScale, duration, listener)
}

fun SubsamplingScaleImageView.animateToScale(scale: Float, duration: Long, listener: SubsamplingScaleImageView.OnAnimationEventListener? = null) {

    if (isReady) {
        val builder = animateScale(scale)
        builder
            ?.withDuration(duration)
            ?.withEasing(EASE_OUT_QUAD)
            ?.withInterruptible(false)
            ?.withOnAnimationEventListener(listener)
            ?.start()
    }
}
