package com.kirkbushman.largeimageview.sampleapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bttn_basic.setOnClickListener {

            val intent = Intent(this, BasicActivity::class.java)
            startActivity(intent)
        }

        bttn_anim.setOnClickListener {

            val intent = Intent(this, AnimFirstActivity::class.java)
            startActivity(intent)
        }
    }
}
