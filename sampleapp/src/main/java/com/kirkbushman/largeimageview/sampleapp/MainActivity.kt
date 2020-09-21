package com.kirkbushman.largeimageview.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kirkbushman.largeimageview.sampleapp.activities.AnimFirstActivity
import com.kirkbushman.largeimageview.sampleapp.activities.BasicActivity
import com.kirkbushman.largeimageview.sampleapp.activities.ImageControlActivity
import com.kirkbushman.largeimageview.sampleapp.activities.ZoomActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bttn_basic.setOnClickListener {

            BasicActivity.start(this)
        }

        bttn_zoom.setOnClickListener {

            ZoomActivity.start(this)
        }

        bttn_control.setOnClickListener {

            ImageControlActivity.start(this)
        }

        bttn_loading.setOnClickListener {

            AnimFirstActivity.start(this, useLoading = true)
        }

        bttn_anim.setOnClickListener {

            AnimFirstActivity.start(this)
        }

        bttn_anim_coil.setOnClickListener {

            AnimFirstActivity.start(this, useCoil = true)
        }
    }
}
