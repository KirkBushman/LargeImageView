package com.kirkbushman.largeimageview.sampleapp.activities

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

abstract class BaseBackActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            doBeforeFinish()

            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        doBeforeFinish()
    }

    override fun onNavigateUp(): Boolean {
        doBeforeFinish()
        return true
    }

    abstract fun doBeforeFinish()
}
