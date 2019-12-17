package com.kirkbushman.largeimageview.sampleapp

import android.content.Intent
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

            val intent = Intent(this, BasicActivity::class.java)
            startActivity(intent)
        }

        bttn_zoom.setOnClickListener {

            val intent = Intent(this, ZoomActivity::class.java)
            startActivity(intent)
        }

        bttn_control.setOnClickListener {

            val intent = Intent(this, ImageControlActivity::class.java)
            startActivity(intent)
        }

        bttn_anim.setOnClickListener {

            val intent = Intent(this, AnimFirstActivity::class.java)
            startActivity(intent)
        }
    }
}
