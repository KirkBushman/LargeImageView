package com.kirkbushman.largeimageview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

@Suppress("unused", "MemberVisibilityCanBePrivate")
class LargeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var loader: ImageLoader? = null

    private var thumbnailUrl: String? = null
    private var sourceUrl: String? = null

    private var thumbnailView: View? = null
    private var sourceView: SubsamplingScaleImageView? = null

    private var showImageWhenAvailable = true

    init {

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LargeImageView, defStyleAttr, 0)

        showImageWhenAvailable = typedArray.getBoolean(R.styleable.LargeImageView_showImageWhenAvailable, true)

        typedArray.recycle()
    }

    fun setImageLoader(loader: ImageLoader) {
        this.loader = loader
    }

    fun getShowImageWhenAvailable(): Boolean {
        return showImageWhenAvailable
    }

    fun setShowImageWhenAvailable(showImageWhenAvailable: Boolean) {
        this.showImageWhenAvailable = showImageWhenAvailable
    }

    fun setThumbnail(thumbnailUrl: String) {
        setImage(thumbnailUrl, null)
    }

    fun setImage(sourceUrl: String) {
        setImage(null, sourceUrl)
    }

    fun setImage(thumbnailUrl: String?, sourceUrl: String?) {
        this.thumbnailUrl = thumbnailUrl
        this.sourceUrl = sourceUrl

        loadImage()
    }

    private fun loadImage() {

        if (loader == null) {
            return
        }

        thumbnailUrl?.let { thumbUrl ->

            thumbnailView = loader!!.getThumbnailView(context)
            thumbnailView?.let { thumbView ->

                loader!!.loadThumbnail(thumbView, thumbUrl)

                addView(thumbnailView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }

        sourceUrl?.let { url ->

            sourceView = SubsamplingScaleImageView(context)
            sourceView?.let { view ->

                loader!!.preloadSource(url)
            }
        }
    }
}
