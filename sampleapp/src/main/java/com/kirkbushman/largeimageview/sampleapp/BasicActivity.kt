package com.kirkbushman.largeimageview.sampleapp

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kirkbushman.largeimageview.ImageLoader
import kotlinx.android.synthetic.main.activity_basic.*

class BasicActivity : AppCompatActivity() {

    companion object {
        private const val SOURCE_URL = "https://external-preview.redd.it/ZcoVuzCSwoXshogcdAPTNsOf0pNCo1FQ6cQ0pqW6A4c.jpg?auto=webp&s=4f7d144d8c6a8da96373ca1b675df140e4c9a0cf"
        private const val THUMB_URL = "https://external-preview.redd.it/ZcoVuzCSwoXshogcdAPTNsOf0pNCo1FQ6cQ0pqW6A4c.jpg?width=320&crop=smart&auto=webp&s=69e043415b9cb786c1e72147faa7acc769b72ba5"
    }

    private val glide by lazy { Glide.with(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        liv_basic.setImageLoader(object : ImageLoader {

            override fun getThumbnailView(context: Context): View {
                return ImageView(context)
            }

            override fun loadThumbnail(view: View, url: String) {
                glide.asBitmap()
                    .load(url)
                    .into(view as ImageView)
            }

            override fun preloadSource(url: String) {
                glide.asBitmap()
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .listener(object : RequestListener<Bitmap> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            e?.printStackTrace()
                            return true
                        }

                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }
                    })
                    .preload()
            }
        })

        liv_basic.setThumbnail(THUMB_URL)
        liv_basic.setImage(SOURCE_URL)
    }
}
