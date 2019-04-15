package xyz.pavelkorolev.githubrepos.services

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ImageLoader(private val context: Context) {

    fun loadCircleAvatar(imageView: ImageView, path: String?) {
        if (path == null) {
            imageView.setImageDrawable(null)
            return
        }
        Glide.with(context)
            .load(path)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }

}