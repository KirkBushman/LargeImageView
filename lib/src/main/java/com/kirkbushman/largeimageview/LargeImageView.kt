package com.kirkbushman.largeimageview

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.UiThread
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.File

@Suppress("unused", "MemberVisibilityCanBePrivate")
class LargeImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var thumbnailUrl: String? = null
    private var sourceUrl: String? = null

    private var thumbnailView: View? = null
    private var errorView: View? = null
    private var sourceView: SubsamplingScaleImageView? = null

    private var loader: ImageLoader? = null
    private var viewsShownListener: OnViewsShownListener? = null

    private var showImageWhenAvailable = true

    init {

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LargeImageView, defStyleAttr, 0)

        showImageWhenAvailable = typedArray.getBoolean(R.styleable.LargeImageView_showImageWhenAvailable, true)

        typedArray.recycle()
    }

    fun setImageLoader(loader: ImageLoader?) {
        this.loader = loader
    }

    fun getSsiv(): SubsamplingScaleImageView? {
        return sourceView
    }

    fun getShowImageWhenAvailable(): Boolean {
        return showImageWhenAvailable
    }

    fun setShowImageWhenAvailable(showImageWhenAvailable: Boolean) {
        this.showImageWhenAvailable = showImageWhenAvailable
    }

    fun setOnViewShownListener(viewsShownListener: OnViewsShownListener?) {
        this.viewsShownListener = viewsShownListener
    }

    fun setImage(thumbnailUrl: String?, sourceUrl: String?) {
        this.thumbnailUrl = thumbnailUrl
        this.sourceUrl = sourceUrl

        startLoading()
    }

    fun triggerShowImage() {

        if (showImageWhenAvailable) {
            return
        }

        showImage()
    }

    private fun startLoading() {

        clearViews()

        loader?.let { loader ->

            // show the thumbnail in the meantime,
            // while the large image is loading in the background.
            thumbnailUrl?.let { thumbUrl ->

                thumbnailView = loader.getThumbnailView(context)
                thumbnailView?.let { thumbView ->

                    loader.loadThumbnail(thumbView, thumbUrl)

                    showThumbnail()
                }
            }

            // start loading in the background the real image.
            sourceUrl?.let { url ->

                sourceView = SubsamplingScaleImageView(context)
                sourceView?.let { view ->

                    loader.preloadSource(url, object : ImageReadyCallback {

                        // if the image is ready set it in the SSIV
                        //
                        // if showImageWhenAvailable is set to true,
                        // show the main image right away
                        //
                        // otherwise wait for triggerShowImage() to get called.
                        //
                        override fun onImageReady(file: File, forceImageShow: Boolean) {

                            view.setImage(ImageSource.uri(Uri.fromFile(file)))

                            if (showImageWhenAvailable || forceImageShow) {
                                showImage()
                            }
                        }

                        // if something went wrong, show the error view.
                        override fun onImageErrored() {
                            errorView = loader.getErrorView(context)
                            errorView?.let { _ ->

                                showErrorView()
                            }
                        }
                    })
                }
            }
        }
    }

    fun clearViews() {
        if (childCount > 0) {
            removeAllViews()
        }

        thumbnailView = null
        errorView = null
        sourceView = null
    }

    @UiThread
    private fun showThumbnail() {

        if (thumbnailView != null) {
            if (findViewById<View>(thumbnailView!!.id) == null) {

                addView(
                    thumbnailView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            viewsShownListener?.onThumbnailViewShown(thumbnailView!!)
        }

        if (thumbnailView?.visibility != View.VISIBLE) {
            thumbnailView?.visibility = View.VISIBLE
        }
    }

    @UiThread
    private fun showErrorView() {

        if (errorView != null) {
            if (findViewById<View>(errorView!!.id) == null) {

                addView(
                    errorView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            viewsShownListener?.onErrorViewShown(errorView!!)
        }

        if (errorView?.visibility != View.VISIBLE) {
            errorView?.visibility = View.VISIBLE
        }

        if (childCount > 0) {

            postDelayed({

                sourceView?.visibility = View.GONE
                thumbnailView?.visibility = View.GONE
            }, 1000)
        }
    }

    @UiThread
    private fun showImage() {

        if (sourceView != null) {
            if (findViewById<View>(sourceView!!.id) == null) {

                addView(
                    sourceView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            viewsShownListener?.onImageViewShown(sourceView!!)
        }

        if (sourceView?.visibility != View.VISIBLE) {
            sourceView?.visibility = View.VISIBLE
        }

        if (childCount > 0) {

            postDelayed({

                errorView?.visibility = View.GONE
                thumbnailView?.visibility = View.GONE
            }, 1000)
        }
    }
}
