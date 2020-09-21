package com.kirkbushman.largeimageview

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
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
        const val SHOWING_LOADING = 2
        const val SHOWING_ERROR = 3
        const val SHOWING_SOURCE = 4

        @IntDef(
            SHOWING_NOTHING,
            SHOWING_THUMBNAIL,
            SHOWING_LOADING,
            SHOWING_ERROR,
            SHOWING_SOURCE
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class State
    }

    private var thumbnailView: View? = null
    private var loadingView: View? = null
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

    fun getLoadingView(): View? {
        return loadingView
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

        showSourceView(false)
    }

    fun startLoading(
        showThumbnail: Boolean = true,
        showLoading: Boolean = true,
        showSource: Boolean = true
    ) {

        clearViews()

        loader?.let { loader ->

            // show the thumbnail in the meantime,
            // while the large image is loading in the background.
            if (showThumbnail) {

                // remember to generate view id
                thumbnailView = buildThumbnailView()
                thumbnailView?.let { thumbView ->

                    loader.loadThumbnail(thumbView)

                    showThumbnailView()
                }
            }

            // show the loading view in the meantime,
            // while the large image is loading in the background.
            if (showLoading) {

                loadingView = buildLoadingView()
                loadingView?.let {

                    showLoadingView()
                }
            }

            // start loading in the background the real image.
            if (showSource) {

                // remember to generate view id
                sourceView = buildSourceView()
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
                                showSourceView(false)
                            }
                        }

                        // if something went wrong, show the error view.
                        override fun onImageErrored() {

                            // remember to generate view id
                            errorView = buildErrorView()
                            errorView?.let { _ ->

                                showErrorView(false)
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
        loadingView = null
        errorView = null
        sourceView = null
    }

    @UiThread
    fun showThumbnailView(disableOtherViews: Boolean = true) {

        if (state == SHOWING_THUMBNAIL) {
            return
        }

        if (thumbnailView == null) {
            thumbnailView = buildThumbnailView()
        }

        thumbnailView?.let {

            addAndEnableChild(it)

            state = SHOWING_THUMBNAIL

            if (disableOtherViews) {

                loadingView?.visibility = View.GONE
                errorView?.visibility = View.GONE
                sourceView?.visibility = View.GONE
            }

            Log.i("LargeImageView", "Showing thumb view")

            viewsShownListener?.onThumbnailViewShown(it)
        }
    }

    @UiThread
    fun showLoadingView(disableOtherViews: Boolean = true) {

        if (state == SHOWING_LOADING) {
            return
        }

        if (loadingView == null) {
            loadingView = buildErrorView()
        }

        loadingView?.let {

            addAndEnableChild(it)

            state = SHOWING_LOADING

            if (disableOtherViews) {

                errorView?.visibility = View.GONE
                thumbnailView?.visibility = View.GONE
                sourceView?.visibility = View.GONE
            }

            viewsShownListener?.onLoadingViewShown(it)
        }
    }

    @UiThread
    fun showErrorView(disableOtherViews: Boolean = true) {

        if (state == SHOWING_ERROR) {
            return
        }

        if (errorView == null) {
            errorView = buildErrorView()
        }

        errorView?.let {

            addAndEnableChild(it)

            state = SHOWING_ERROR

            if (disableOtherViews) {

                loadingView?.visibility = View.GONE
                thumbnailView?.visibility = View.GONE
                sourceView?.visibility = View.GONE
            }

            viewsShownListener?.onErrorViewShown(it)
        }
    }

    @UiThread
    fun showSourceView(disableOtherViews: Boolean = true) {

        if (state == SHOWING_SOURCE) {
            return
        }

        if (sourceView == null) {
            sourceView = buildSourceView()
        }

        sourceView?.let {

            addAndEnableChild(it)

            state = SHOWING_SOURCE

            if (disableOtherViews) {

                thumbnailView?.visibility = View.GONE
                loadingView?.visibility = View.GONE
                errorView?.visibility = View.GONE
            }

            Log.i("LargeImageView", "Showing source view")

            viewsShownListener?.onImageViewShown(it)
        }
    }

    private fun addAndEnableChild(view: View) {

        if (findViewById<View>(view.id) == null) {

            addView(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
        }

        if (childCount > 0) {
            view.bringToFront()
        }
    }

    private fun buildThumbnailView(): View? {
        val view = loader?.getThumbnailView(context)
        view?.id = View.generateViewId()

        return view
    }

    private fun buildSourceView(): SubsamplingScaleImageView {
        val view = SubsamplingScaleImageView(context)
        view.id = View.generateViewId()

        return view
    }

    private fun buildLoadingView(): View? {
        val view = loader?.getLoadingView(context)
        view?.id = View.generateViewId()

        return view
    }

    private fun buildErrorView(): View? {
        val view = loader?.getErrorView(context)
        view?.id = View.generateViewId()

        return view
    }
}
