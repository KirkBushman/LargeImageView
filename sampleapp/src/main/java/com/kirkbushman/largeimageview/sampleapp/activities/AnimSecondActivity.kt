package com.kirkbushman.largeimageview.sampleapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.kirkbushman.largeimageview.ImageLoader
import com.kirkbushman.largeimageview.ImageReadyCallback
import com.kirkbushman.largeimageview.sampleapp.R
import com.kirkbushman.largeimageview.sampleapp.utils.doAsync
import com.kirkbushman.largeimageview.sampleapp.utils.loadThumbnail
import kotlinx.android.synthetic.main.activity_anim_second.*
import java.io.File
import java.lang.Exception

class AnimSecondActivity : AppCompatActivity() {

    companion object {

        private const val PARAM_THUMB = "intent_param_thumb"
        private const val PARAM_SOURCE = "intent_param_source"

        fun start(activity: Activity, imageView: ImageView, thumbUrl: String, sourceUrl: String) {
            val intent = Intent(activity, AnimSecondActivity::class.java)
            intent.putExtra(PARAM_THUMB, thumbUrl)
            intent.putExtra(PARAM_SOURCE, sourceUrl)

            val transitionName = activity.resources.getString(R.string.image_transition_name)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, transitionName)
            val bundle = options.toBundle()

            activity.startActivity(intent, bundle)
        }
    }

    private val thumbUrl by lazy { intent.getStringExtra(PARAM_THUMB) }
    private val sourceUrl by lazy { intent.getStringExtra(PARAM_SOURCE) }

    private val glide by lazy { Glide.with(this) }

    private var transitionEnded = false
    private var imageDownloaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_second)

        if (Build.VERSION.SDK_INT >= 21) {
            window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {

                override fun onTransitionStart(transition: Transition?) {}
                override fun onTransitionCancel(transition: Transition?) {}
                override fun onTransitionPause(transition: Transition?) {}
                override fun onTransitionResume(transition: Transition?) {}

                override fun onTransitionEnd(transition: Transition?) {

                    transitionEnded = true

                    if (imageDownloaded) {

                        liv.triggerShowImage()
                    }

                    window.sharedElementEnterTransition.removeListener(this)
                }
            })
        }

        liv.setImageLoader(object : ImageLoader {

            override fun getThumbnailView(context: Context): View {
                return ImageView(context)
            }

            override fun loadThumbnail(view: View, url: String) {
                glide.loadThumbnail(url, view as ImageView)
            }

            override fun getErrorView(context: Context): View? {
                return null
            }

            override fun preloadSource(url: String, callback: ImageReadyCallback) {

                var file: File? = null

                doAsync(doWork = {

                    try {

                        file = glide.downloadOnly()
                            .load(url)
                            .submit()
                            .get()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }, onPost = {

                    if (file != null) {
                        callback.onImageReady(file!!, transitionEnded)
                    } else {
                        callback.onImageErrored()
                    }

                    imageDownloaded = true
                })
            }
        })

        liv.setImage(thumbUrl, sourceUrl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResultAndFinish()
    }

    override fun onNavigateUp(): Boolean {
        setResultAndFinish()
        return true
    }

    private fun setResultAndFinish() {
        if (Build.VERSION.SDK_INT >= 21) {
            finishAfterTransition()
        }
    }
}
