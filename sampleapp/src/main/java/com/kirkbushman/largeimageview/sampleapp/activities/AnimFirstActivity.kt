package com.kirkbushman.largeimageview.sampleapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.bumptech.glide.Glide
import com.kirkbushman.largeimageview.sampleapp.R
import com.kirkbushman.largeimageview.sampleapp.utils.loadThumbnail
import kotlinx.android.synthetic.main.activity_anim_first.*

class AnimFirstActivity : AppCompatActivity() {

    companion object {

        private const val THUMB_URL = "https://preview.redd.it/kgwb58shlp441.jpg?width=320&crop=smart&auto=webp&s=592da75578ac15f59ed762057138d62add0a18d7"
        private const val SOURCE_URL = "https://preview.redd.it/kgwb58shlp441.jpg?auto=webp&s=391b7b139416a8a0d11b5528b13b8e49b124a341"

        private const val FLAG_USE_COIL = "intent_extra_flag_use_coil"

        fun start(context: Context, useCoil: Boolean = false) {
            val intent = Intent(context, AnimFirstActivity::class.java)
            intent.putExtra(FLAG_USE_COIL, useCoil)

            context.startActivity(intent)
        }
    }

    private val useCoil by lazy { intent.getBooleanExtra(FLAG_USE_COIL, false) }

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

            AnimSecondActivity.start(this, thumbnail, THUMB_URL, SOURCE_URL)
        }
    }
}
