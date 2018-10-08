package com.jneuberger.imagesearch.flux.action

import android.content.Context
import com.jneuberger.imagesearch.entity.Image

interface DownloadActions {
    fun downloadImage(context: Context, image: Image)
    fun downloadMultipleImages(context: Context, imageList: List<Image>)
    fun notifyDownloadComplete(downloadId: Long)

    companion object {
        const val DOWNLOAD_IMAGE = "downloadImage"
        const val DOWNLOAD_MULTIPLE_IMAGES = "downloadMultipleImages"
        const val NOTIFY_DOWNLOAD_COMPLETE = "notifyDownloadComplete"
    }
}