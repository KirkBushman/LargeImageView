package com.kirkbushman.largeimageview.sampleapp

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val options = RequestOptions()
            .disallowHardwareConfig()
            .format(if (activityManager.isLowRamDevice)
                DecodeFormat.PREFER_RGB_565
            else
                DecodeFormat.PREFER_ARGB_8888)

        builder.setLogLevel(Log.VERBOSE)
            .setDefaultRequestOptions(options)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
