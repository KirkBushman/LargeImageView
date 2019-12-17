LargeImageView
=====

LargeImageView is a custom view for Android, written in Kotlin, helping you manage a [SubsamplingImageView](https://github.com/davemorrissey/subsampling-scale-image-view).

With LIV is easy to show a thumbnail while loading the source image in the background,
show an error view in case something goes wrong.

LIV makes it easy to use the subsampling, zooming and gestures capabilities of SSIV in combination with a transition or shared-element-transition,
allowing you to animate the thumbnail and delay the loading of source image until the animation is complete.

Check out the sample app, to learn how to make it work with [Glide](https://github.com/bumptech/glide).


### How to install.

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.KirkBushman:LargeImageView:Tag'
}
```

### Usage

To manage the behaviour of LIV you must provide an ImageLoader, like so:

```

liv.setImageLoader(object : ImageLoader {

    override fun getThumbnailView(context: Context): View {
        ...
    }

    override fun loadThumbnail(view: View, url: String) {
        ...
    }

    override fun getErrorView(context: Context): View {
        ...
    }

    override fun preloadSource(url: String, callback: ImageReadyCallback) {
        ...
        callback.onImageReady(file)
        ...
        callback.onImageErrored()
    }
})

```

after doing that, start the process with:

```
liv.startLoading()
```

It's that simple!


### License
This project is licensed under the MIT License
