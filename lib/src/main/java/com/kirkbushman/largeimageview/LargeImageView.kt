package com.kirkbushman.largeimageview

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.annotation.UiThread
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.File

@Suppress("unused", "MemberVisibilityCanBePrivate")
class LargeImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {

        const val SHOWING_NOTHING = 0
        const val SHOWING_THUMBNAIL = 1
        const val SHOWING_ERROR = 2
        const val SHOWING_SOURCE = 3

        @IntDef(SHOWING_NOTHING, SHOWING_THUMBNAIL, SHOWING_ERROR, SHOWING_SOURCE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class State
    }

    private var thumbnailView: View? = null
    private var errorView: View? = null
    private var sourceView: SubsamplingScaleImageView? = null

    private var loader: ImageLoader? = null
    private var viewsShownListener: OnViewsShownListener? = null

    @State
    private var state: Int = SHOWING_NOTHING

    private var showImageWhenAvailable: Boolean

    init {

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LargeImageView, defStyleAttr, 0)

        showImageWhenAvailable = typedArray.getBoolean(R.styleable.LargeImageView_showImageWhenAvailable, true)

        typedArray.recycle()
    }

    fun setImageLoader(loader: ImageLoader?) {
        this.loader = loader
    }

    fun getThumbnailView(): View? {
        return thumbnailView
    }

    fun getErrorView(): View? {
        return errorView
    }

    fun getSsiv(): SubsamplingScaleImageView? {
        return sourceView
    }

    @State
    fun getState(): Int {
        return state
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

    fun triggerShowImage() {

        if (showImageWhenAvailable) {
            return
        }

        showImage()
    }

    fun startLoading(showThumbnail: Boolean = true, showSource: Boolean = true) {

        clearViews()

        loader?.let { loader ->

            // show the thumbnail in the meantime,
            // while the large image is loading in the background.
            if (showThumbnail) {

                thumbnailView = loader.getThumbnailView(context)
                thumbnailView?.let { thumbView ->

                    loader.loadThumbnail(thumbView)

                    showThumbnail()
                }
            }

            // start loading in the background the real image.
            if (showSource) {

                sourceView = SubsamplingScaleImageView(context)
                sourceView?.let { view ->

                    loader.preloadSource(object : ImageReadyCallback {

                        // if the image is ready set it in the SSIV
                        //
                        // if showImageWhenAvailable is set to true,
                        // show the main image right away
                        //
                        // otherwise wait for triggerShowImage() to get called.
                        //
                        override fun onImageReady(file: File) {

                            view.setImage(ImageSource.uri(Uri.fromFile(file)))

                            if (showImageWhenAvailable) {
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

        state = SHOWING_NOTHING

        thumbnailView = null
        errorView = null
        sourceView = null
    }

    @UiThread
    private fun showThumbnail() {

        if (state == SHOWING_THUMBNAIL) {
            return
        }

        thumbnailView?.let {

            addChildIfNotPresent(it)
            enableChildView(it)

            state = SHOWING_THUMBNAIL

            viewsShownListener?.onThumbnailViewShown(it)
        }
    }

    @UiThread
    private fun showErrorView() {

        if (state == SHOWING_ERROR) {
            return
        }

        errorView?.let {

            addChildIfNotPresent(it)
            enableChildView(it)

            state = SHOWING_ERROR

            viewsShownListener?.onErrorViewShown(it)
        }
    }

    @UiThread
    private fun showImage() {

        if (state == SHOWING_SOURCE) {
            return
        }

        sourceView?.let {

            addChildIfNotPresent(it)
            enableChildView(it)

            state = SHOWING_SOURCE

            viewsShownListener?.onImageViewShown(it)
        }
    }

    private fun addChildIfNotPresent(view: View) {

        if (findViewById<View>(view.id) == null) {

            addView(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private fun enableChildView(view: View) {

        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
        }

        if (childCount > 0) {
            view.bringToFront()
        }
    }
}
