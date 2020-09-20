package com.kirkbushman.largeimageview.sampleapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kirkbushman.largeimageview.ImageLoader
import com.kirkbushman.largeimageview.ImageReadyCallback
import com.kirkbushman.largeimageview.sampleapp.R
import com.kirkbushman.largeimageview.sampleapp.utils.Utils
import com.kirkbushman.largeimageview.sampleapp.utils.doAsync
import kotlinx.android.synthetic.main.activity_image_control.*
import java.io.File
import java.lang.Exception

class ImageControlActivity : AppCompatActivity() {

    companion object {

        private const val THUMB_URL = "https://preview.redd.it/rqmgi2lmkcm31.jpg?width=320&crop=smart&auto=webp&s=2960f224346941ccb6b01455141a40b6367a62e3"
        private const val SOURCE_URL = "https://preview.redd.it/rqmgi2lmkcm31.jpg?auto=webp&s=db751ffa3a88b2e91de577fa4b1459eb7ef9b7c3"

        fun start(context: Context) {
            val intent = Intent(context, ImageControlActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val glide by lazy { Glide.with(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_control)

        bttn_show_thumbnail.setOnClickListener {

            liv.showThumbnailView()
        }

        bttn_show_source.setOnClickListener {

            liv.showSourceView()
        }

        bttn_show_error.setOnClickListener {

            liv.showErrorView()
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
