package com.jneuberger.imagesearch.flux.action.creator

import android.content.Context
import com.jneuberger.imagesearch.entity.Image
import com.jneuberger.imagesearch.flux.Keys.CONTEXT_KEY
import com.jneuberger.imagesearch.flux.Keys.DOWNLOAD_ID_KEY
import com.jneuberger.imagesearch.flux.Keys.IMAGE_KEY
import com.jneuberger.imagesearch.flux.Keys.IMAGE_LIST_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.DownloadActions
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.DOWNLOAD_IMAGE
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.DOWNLOAD_MULTIPLE_IMAGES
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.NOTIFY_DOWNLOAD_COMPLETE
import java.util.*

class DownloadActionsCreator : Observable(), DownloadActions {
    override fun downloadImage(context: Context, image: Image) = setChanged().run {
        notifyObservers(Action(DOWNLOAD_IMAGE, hashMapOf(CONTEXT_KEY to context, IMAGE_KEY to image)))
    }

    override fun downloadMultipleImages(context: Context, imageList: List<Image>) = setChanged().run {
        notifyObservers(Action(DOWNLOAD_MULTIPLE_IMAGES, hashMapOf(CONTEXT_KEY to context, IMAGE_LIST_KEY to imageList)))
    }

    override fun notifyDownloadComplete(downloadId: Long) = setChanged().run {
        notifyObservers(Action(NOTIFY_DOWNLOAD_COMPLETE, hashMapOf(DOWNLOAD_ID_KEY to downloadId)))
    }

    companion object {
        val instance: DownloadActionsCreator by lazy { DownloadActionsCreator() }
    }
}