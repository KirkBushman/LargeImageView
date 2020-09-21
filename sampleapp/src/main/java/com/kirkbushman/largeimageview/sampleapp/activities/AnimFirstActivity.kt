package com.kirkbushman.largeimageview.sampleapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.bumptech.glide.Glide
import com.kirkbushman.largeimageview.sampleapp.R
import com.kirkbushman.largeimageview.sampleapp.utils.loadThumbnail
import kotlinx.android.synthetic.main.activity_anim_first.*

class AnimFirstActivity : AppCompatActivity() {

    companion object {

        private const val THUMB_URL = "https://preview.redd.it/kgwb58shlp441.jpg?width=320&crop=smart&auto=webp&s=592da75578ac15f59ed762057138d62add0a18d7"
        private const val SOURCE_URL = "https://preview.redd.it/kgwb58shlp441.jpg?auto=webp&s=391b7b139416a8a0d11b5528b13b8e49b124a341"

        // private const val SOURCE_URL = "https://i.redd.it/vbm1ft0gzdo51.jpg"

        // private const val THUMB_URL = "https://preview.redd.it/9qcfzckaib531.jpg?width=216&crop=smart&auto=webp&s=63d71098f774fcc0dffe793e563fc7c5139671e4"
        // private const val SOURCE_URL = "https://i.redd.it/9qcfzckaib531.jpg"

        private const val FLAG_USE_COIL = "intent_extra_flag_use_coil"
        private const val FLAG_USE_LOADING = "intent_extra_flag_use_loading"

        fun start(
            context: Context,
            useCoil: Boolean = false,
            useLoading: Boolean = false
        ) {
            val intent = Intent(context, AnimFirstActivity::class.java)
            intent.putExtra(FLAG_USE_COIL, useCoil)
            intent.putExtra(FLAG_USE_LOADING, useLoading)

            context.startActivity(intent)
        }
    }

    private val useCoil by lazy { intent.getBooleanExtra(FLAG_USE_COIL, false) }
    private val useLoading by lazy { intent.getBooleanExtra(FLAG_USE_LOADING, false) }

    private val glide by lazy { Glide.with(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_first)

        if (useCoil) {
            thumbnail.load(THUMB_URL)
        } else {
            glide.loadThumbnail(THUMB_URL, thumbnail)
        }

        thumbnail.setOnClickListener {

            val thumbUrl = if (useLoading) {
                null
            } else {
                THUMB_URL
            }

            AnimSecondActivity.start(
                activity = this,
                imageView = thumbnail,
                thumbUrl = thumbUrl,
                sourceUrl = SOURCE_URL,
                sharedAnim = !useLoading
            )
        }
    }
}
