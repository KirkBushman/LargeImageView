package com.kirkbushman.largeimageview.sampleapp.test

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.kirkbushman.largeimageview.ImageLoader
import com.kirkbushman.largeimageview.ImageReadyCallback
import com.kirkbushman.largeimageview.LargeImageView
import com.kirkbushman.largeimageview.OnViewsShownListener
import com.kirkbushman.largeimageview.sampleapp.utils.doAsync
import com.kirkbushman.largeimageview.sampleapp.utils.loadThumbnail
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class LargeImageViewTest {

    companion object {

        private const val URL_PREVIEW = "https://preview.redd.it/9sknc9l5m7251.jpg?width=640&crop=smart&auto=webp&s=4a3328569dfe9167e18b13762e87a89520af8dcf"
        private const val URL_SOURCE = "https://i.redd.it/9sknc9l5m7251.jpg"

        private const val ERR_PREVIEW = "https://google.com/12345_preview.jpg"
        private const val ERR_SOURCE = "https://google.com/12345_source.jpg"
    }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context by lazy { InstrumentationRegistry.getInstrumentation().targetContext.applicationContext }

    @Test
    @UiThreadTest
    fun testSuccessfulImageLoad() {

        val glide = Glide.with(context)
        val liv = LargeImageView(context)

        liv.setOnViewShownListener(object : OnViewsShownListener {

            override fun onThumbnailViewShown(view: View) {
                assertTrue("Checking the view is a ImageView", view is ImageView)
                assertTrue("Checking the view is visible", view.visibility == View.VISIBLE)

                liv.triggerShowImage()
            }

            override fun onErrorViewShown(view: View) {
                assertTrue("Checking this is not called", false)
            }

            override fun onImageViewShown(view: SubsamplingScaleImageView) {
                assertTrue("Checking the view is a ImageView", view.hasImage())
            }
        })

        liv.setImageLoader(object : ImageLoader {

            override fun getThumbnailView(context: Context): View? {
                return ImageView(context)
            }

            override fun loadThumbnail(view: View) {
                glide.loadThumbnail(URL_PREVIEW, view as ImageView)
            }

            override fun getErrorView(context: Context): View? {
                val errorText = TextView(context)
                errorText.text = "There was an Error!"
                errorText.typeface = Typeface.DEFAULT_BOLD
                errorText.textSize = 21f
                errorText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                errorText.gravity = Gravity.CENTER

                return errorText
            }

            override fun preloadSource(callback: ImageReadyCallback) {

                var file: File? = null

                doAsync(doWork = {

                    try {

                        file = glide.downloadOnly()
                            .load(URL_SOURCE)
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

        liv.startLoading()

        Thread.sleep(5000)
    }

    @Test
    @UiThreadTest
    fun testUnsuccessfulImageLoad() {

        val glide = Glide.with(context)
        val liv = LargeImageView(context)

        liv.setOnViewShownListener(object : OnViewsShownListener {

            override fun onThumbnailViewShown(view: View) {
                assertTrue("Checking the view is a ImageView", view is ImageView)
                assertTrue("Checking the view is visible", view.visibility == View.VISIBLE)

                liv.triggerShowImage()
            }

            override fun onErrorViewShown(view: View) {
                assertTrue("Checking this is called", true)
            }

            override fun onImageViewShown(view: SubsamplingScaleImageView) {
                assertTrue("Checking this is not called", false)
            }
        })

        liv.setImageLoader(object : ImageLoader {

            override fun getThumbnailView(context: Context): View? {
                return ImageView(context)
            }

            override fun loadThumbnail(view: View) {
                glide.loadThumbnail(ERR_PREVIEW, view as ImageView)
            }

            override fun getErrorView(context: Context): View? {
                val errorText = TextView(context)
                errorText.text = "There was an Error!"
                errorText.typeface = Typeface.DEFAULT_BOLD
                errorText.textSize = 21f
                errorText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                errorText.gravity = Gravity.CENTER

                return errorText
            }

            override fun preloadSource(callback: ImageReadyCallback) {

                var file: File? = null

                doAsync(doWork = {

                    try {

                        file = glide.downloadOnly()
                            .load(ERR_SOURCE)
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

        liv.startLoading()

        Thread.sleep(5000)
    }
}
