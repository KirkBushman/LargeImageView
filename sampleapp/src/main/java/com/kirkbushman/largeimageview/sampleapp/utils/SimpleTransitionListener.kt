package com.kirkbushman.largeimageview.sampleapp.utils

import android.transition.Transition

abstract class SimpleTransitionListener : Transition.TransitionListener {

    override fun onTransitionStart(transition: Transition?) {}
    override fun onTransitionPause(transition: Transition?) {}
    override fun onTransitionCancel(transition: Transition?) {}
    override fun onTransitionResume(transition: Transition?) {}
    override fun onTransitionEnd(transition: Transition?) {}
}
