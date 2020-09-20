package com.kirkbushman.largeimageview.sampleapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kirkbushman.largeimageview.*
import com.kirkbushman.largeimageview.sampleapp.R
import com.kirkbushman.largeimageview.sampleapp.utils.Utils
import com.kirkbushman.largeimageview.sampleapp.utils.doAsync
import kotlinx.android.synthetic.main.activity_zoom.*
import kotlinx.android.synthetic.main.activity_zoom.liv
import java.io.File
import java.lang.Exception

class ZoomActivity : AppCompatActivity() {

    companion object {

        private const val THUMB_URL = "https://preview.redd.it/c24zusxca3x31.jpg?width=320&crop=smart&auto=webp&s=0f739cfc03bd6cea032e03a283140ccce2aa6b9b"
        private const val SOURCE_URL = "https://preview.redd.it/c24zusxca3x31.jpg?auto=webp&s=06ec0f3d46494bc3c4afe9596cc57e9810d7719e"

        private const val ANIM_DURATION = 400L

        fun start(context: Context) {
            val intent = Intent(context, ZoomActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val glide by lazy { Glide.with(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom)

        bttn_min_scale.setOnClickListener {

            liv.getSsiv()?.scaleMin(ANIM_DURATION)
        }

        bttn_half_scale.setOnClickListener {

            val ssiv = liv.getSsiv()
            val halfScale = (((ssiv?.maxScale ?: 0f) - (ssiv?.minScale ?: 0f)) / 2f) + (ssiv?.minScale ?: 0f)
            ssiv?.animateToScale(halfScale, ANIM_DURATION)
        }

        bttn_max_scale.setOnClickListener {

            liv.getSsiv()?.scaleMax(ANIM_DURATION)
        }

        liv.setImageLoader(object : ImageLoader {

            override fun getThumbnailView(context: Context): View? {
                return ImageView(context)
            }

            override fun loadThumbnail(view: View) {
                glide.asBitmap()
                    .load(THUMB_URL)
                    .into(view as ImageView)
            }

            override fun getErrorView(context: Context): View? {
                return Utils.getErrorView(context)
            }

            override fun preloadSource(callback: ImageReadyCallback) {

                var file: File? = null

                doAsync(
                    doWork = {

                        try {

                            file = glide.downloadOnly()
                                .load(SOURCE_URL)
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

        liv.startLoading()
    }
}
