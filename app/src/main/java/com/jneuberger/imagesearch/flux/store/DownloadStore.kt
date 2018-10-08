package com.jneuberger.imagesearch.flux.store

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.jneuberger.imagesearch.entity.Image
import com.jneuberger.imagesearch.flux.Keys.CONTEXT_KEY
import com.jneuberger.imagesearch.flux.Keys.DOWNLOAD_ID_KEY
import com.jneuberger.imagesearch.flux.Keys.IMAGE_KEY
import com.jneuberger.imagesearch.flux.Keys.IMAGE_LIST_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.DOWNLOAD_IMAGE
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.DOWNLOAD_MULTIPLE_IMAGES
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.NOTIFY_DOWNLOAD_COMPLETE
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class DownloadStore : Observable(), Observer {
    private var mDownloadId: Long? = null
    var imagesDownloading = HashMap<Long, Image>()

    override fun update(o: Observable?, arg: Any?) {
        arg ?: run { return }
        val action: Action? = arg as Action
        when (action?.type) {
            DOWNLOAD_IMAGE -> downloadImage((action.data!![CONTEXT_KEY] as Context), action.data[IMAGE_KEY] as Image)
            DOWNLOAD_MULTIPLE_IMAGES -> (action.data!![IMAGE_LIST_KEY] as List<Image>).map {
                downloadImage((action.data[CONTEXT_KEY] as Context), it)
            }
            NOTIFY_DOWNLOAD_COMPLETE -> {
                imagesDownloading[action.data!![DOWNLOAD_ID_KEY]]?.apply {
                    isDownloaded = true
                    isDownloading = false
                }
                setChanged().run { notifyObservers(Reaction(NOTIFY_DOWNLOAD_COMPLETE)) }
            }
        }
    }

    private fun downloadImage(context: Context, image: Image) {
        val imageDirectory = File(Environment.getExternalStorageDirectory(), DIRECTORY_IMAGE_SEARCH)
        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs()
        }
        val request = DownloadManager.Request(Uri.parse(image.downloadLink)).apply {
            setDestinationInExternalPublicDir(DIRECTORY_IMAGE_SEARCH, "$IMAGE_BY_PREFIX${image.user.replace(" ", "")}$JPG_EXTENSION")
        }
        mDownloadId = (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
        imagesDownloading[mDownloadId!!] = image
        setChanged().run { notifyObservers(Reaction(DOWNLOAD_IMAGE)) }
    }

    companion object {
        private const val DIRECTORY_IMAGE_SEARCH = "/ImageSearch"
        private const val IMAGE_BY_PREFIX = "imageBy"
        private const val JPG_EXTENSION = ".jpg"
        val instance: DownloadStore by lazy { DownloadStore() }
    }
}