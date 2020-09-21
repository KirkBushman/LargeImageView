package com.kirkbushman.largeimageview.sampleapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.app.ActivityOptionsCompat
import coil.load
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.kirkbushman.largeimageview.ImageLoader
import com.kirkbushman.largeimageview.ImageReadyCallback
import com.kirkbushman.largeimageview.OnViewsShownListener
import com.kirkbushman.largeimageview.sampleapp.R
import com.kirkbushman.largeimageview.sampleapp.utils.SimpleTransitionListener
import com.kirkbushman.largeimageview.sampleapp.utils.doAsync
import com.kirkbushman.largeimageview.sampleapp.utils.loadThumbnail
import com.kirkbushman.largeimageview.scaleMin
import kotlinx.android.synthetic.main.activity_anim_second.*
import java.io.File
import java.lang.Exception

class AnimSecondActivity : BaseBackActivity() {

    companion object {

        private const val PARAM_THUMB = "intent_param_thumb"
        private const val PARAM_SOURCE = "intent_param_source"

        private const val FLAG_USE_COIL = "intent_extra_flag_use_coil2"
        private const val FLAG_SHARED_ANIM = "intent_extra_flag_shared_anim"

        private const val ANIM_DURATION = 300L

        fun start(
            activity: Activity,
            imageView: ImageView,

            thumbUrl: String?,
            sourceUrl: String,

            useCoil: Boolean = false,
            sharedAnim: Boolean = true
        ) {

            val intent = Intent(activity, AnimSecondActivity::class.java)
            intent.putExtra(PARAM_THUMB, thumbUrl)
            intent.putExtra(PARAM_SOURCE, sourceUrl)
            intent.putExtra(FLAG_USE_COIL, useCoil)
            intent.putExtra(FLAG_SHARED_ANIM, sharedAnim)

            if (sharedAnim) {

                val transitionName = activity.resources.getString(R.string.image_transition_name)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, transitionName)
                val bundle = options.toBundle()

                activity.startActivity(intent, bundle)
            } else {

                activity.startActivity(intent)
            }
        }
    }

    private val thumbUrl by lazy { intent.getStringExtra(PARAM_THUMB) }
    private val sourceUrl by lazy { intent.getStringExtra(PARAM_SOURCE) }

    private val useCoil by lazy { intent.getBooleanExtra(FLAG_USE_COIL, false) }
    private val sharedAnim by lazy { intent.getBooleanExtra(FLAG_SHARED_ANIM, true) }

    private val glide by lazy { Glide.with(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_second)

        if (Build.VERSION.SDK_INT >= 21) {

            if (sharedAnim) {

                window.sharedElementEnterTransition.addListener(
                    object : SimpleTransitionListener() {

                        override fun onTransitionEnd(transition: Transition?) {

                            liv.triggerShowImage()

                            window.sharedElementEnterTransition.removeListener(this)
                        }
                    }
                )
            }
        }

        liv.setShowImageWhenAvailable(!sharedAnim)
        liv.setImageLoader(object : ImageLoader {

            override fun getThumbnailView(context: Context): View {
                return ImageView(context)
            }

            override fun loadThumbnail(view: View) {
                if (useCoil) {
                    (view as ImageView).load(thumbUrl)
                } else {
                    glide.loadThumbnail(thumbUrl!!, view as ImageView)
                }
            }

            override fun getLoadingView(context: Context): View? {
                return ProgressBar(context)
            }

            override fun getErrorView(context: Context): View? {
                return null
            }

            override fun preloadSource(callback: ImageReadyCallback) {

                var file: File? = null

                doAsync(
                    doWork = {

                        try {

                            file = glide.downloadOnly()
                                .load(sourceUrl)
                                .submit()
                                .get()
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    },
                    onPost = {

                        if (file != null) {
                            callback.onImageReady(file!!)
                        } else {
                            callback.onImageErrored()
                        }
                    }
                )
            }
        })

        liv.setOnViewShownListener(
            object : OnViewsShownListener {

                override fun onThumbnailViewShown(view: View) {
                    Log.i("AnimSecondActivity", "onThumbnailViewShown")
                }
                override fun onLoadingViewShown(view: View) {
                    Log.i("AnimSecondActivity", "onLoadingViewShown")
                }
                override fun onErrorViewShown(view: View) {
                    Log.i("AnimSecondActivity", "onErrorViewShown")
                }
                override fun onImageViewShown(view: SubsamplingScaleImageView) {
                    Log.i("AnimSecondActivity", "onImageViewShown")
                }
            }
        )

        liv.startLoading(
            showThumbnail = thumbUrl != null,
            showLoading = thumbUrl == null,
            showSource = true
        )
    }

    override fun doBeforeFinish() {

        if (Build.VERSION.SDK_INT >= 21) {

            val ssiv = liv.getSsiv()
            if (ssiv?.scale == ssiv?.minScale) {

                finishAfterTransition()
            } else {

                ssiv?.scaleMin(
                    duration = ANIM_DURATION,
                    listener = object : SubsamplingScaleImageView.OnAnimationEventListener {

                        override fun onComplete() {
                            finishAfterTransition()
                        }

                        override fun onInterruptedByNewAnim() {
                            finishAfterTransition()
                        }

                        override fun onInterruptedByUser() {
                            finishAfterTransition()
                        }
                    }
                )
            }
        }
    }

    override fun finishAfterTransition() {
        liv.showThumbnailView()
        super.finishAfterTransition()
    }
}
