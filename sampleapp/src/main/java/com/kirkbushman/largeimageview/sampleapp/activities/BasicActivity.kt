package com.kirkbushman.largeimageview.sampleapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.kirkbushman.largeimageview.ImageLoader
import com.kirkbushman.largeimageview.ImageReadyCallback
import com.kirkbushman.largeimageview.OnViewsShownListener
import com.kirkbushman.largeimageview.sampleapp.R
import com.kirkbushman.largeimageview.sampleapp.utils.Utils
import com.kirkbushman.largeimageview.sampleapp.utils.doAsync
import com.kirkbushman.largeimageview.sampleapp.utils.showToast
import kotlinx.android.synthetic.main.activity_basic.*
import java.io.File
import java.lang.Exception

class BasicActivity : AppCompatActivity() {

    companion object {

        private const val SOURCE_URL = "https://external-preview.redd.it/ZcoVuzCSwoXshogcdAPTNsOf0pNCo1FQ6cQ0pqW6A4c.jpg?auto=webp&s=4f7d144d8c6a8da96373ca1b675df140e4c9a0cf"
        private const val THUMB_URL = "https://external-preview.redd.it/ZcoVuzCSwoXshogcdAPTNsOf0pNCo1FQ6cQ0pqW6A4c.jpg?width=320&crop=smart&auto=webp&s=69e043415b9cb786c1e72147faa7acc769b72ba5"

        fun start(context: Context) {
            val intent = Intent(context, BasicActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val glide by lazy { Glide.with(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        liv_basic.setImageLoader(object : ImageLoader {

            override fun getThumbnailView(context: Context): View {
                return ImageView(context)
            }

            override fun loadThumbnail(view: View) {
                glide.asBitmap()
                    .load(THUMB_URL)
                    .into(view as ImageView)
            }

            override fun getErrorView(context: Context): View {
                return Utils.getErrorView(context)
            }

            override fun preloadSource(callback: ImageReadyCallback) {

                var file: File? = null

                doAsync(doWork = {

                    try {

                        file = glide.downloadOnly()
                            .load(SOURCE_URL)
                            .submit()
                            .get()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }, onPost = {

                    if (file != null) {
                        callback.onImageReady(file!!)
                    } else {
                        callback.onImageErrored()
                    }
                })
            }
        })

        liv_basic.setOnViewShownListener(object : OnViewsShownListener {

            override fun onThumbnailViewShown(view: View) {
                showToast("Thumbnail Shown!")
            }

            override fun onErrorViewShown(view: View) {
                showToast("Error Shown!")
            }

            override fun onImageViewShown(view: SubsamplingScaleImageView) {
                showToast("Image Shown!")
            }
        })

        liv_basic.startLoading()
    }
}
