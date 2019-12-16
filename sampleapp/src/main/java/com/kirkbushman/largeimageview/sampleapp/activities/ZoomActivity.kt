package com.kirkbushman.largeimageview.sampleapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kirkbushman.largeimageview.sampleapp.R
import kotlinx.android.synthetic.main.activity_zoom.*

class ZoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom)

        bttn_min_scale.setOnClickListener {


        }

        bttn_half_scale.setOnClickListener {


        }

        bttn_max_scale.setOnClickListener {


        }
    }
}