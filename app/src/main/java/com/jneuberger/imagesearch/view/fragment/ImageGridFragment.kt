package com.jneuberger.imagesearch.view.fragment

import android.Manifest
import android.app.DownloadManager
import android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.entity.Image
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.ENABLE_EDIT_MODE
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.DOWNLOAD_IMAGE
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.NOTIFY_DOWNLOAD_COMPLETE
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.SEARCH_BY_TERM
import com.jneuberger.imagesearch.flux.action.creator.AppActionsCreator
import com.jneuberger.imagesearch.flux.action.creator.DownloadActionsCreator
import com.jneuberger.imagesearch.flux.store.AppStore
import com.jneuberger.imagesearch.flux.store.DownloadStore
import com.jneuberger.imagesearch.flux.store.Reaction
import com.jneuberger.imagesearch.flux.store.SearchStore
import com.jneuberger.imagesearch.view.adapter.ImageListAdapter
import kotlinx.android.synthetic.main.fragment_image_grid.*
import java.util.*


class ImageGridFragment : Fragment(), Observer {
    private var mAppActionsCreator = AppActionsCreator.instance
    private var mAppStore = AppStore.instance
    private var mDownloadActionsCreator = DownloadActionsCreator.instance
    private var mDownloadStore = DownloadStore.instance
    private var mSearchStore = SearchStore.instance
    private lateinit var mImageListAdapter: ImageListAdapter
    private val mImagesToDownloadList = ArrayList<Image>()

    private val mDownloadCompletionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (mDownloadStore.imagesDownloading.keys.contains(id)) {
                mDownloadActionsCreator.notifyDownloadComplete(id)
            }
            if (mDownloadStore.imagesDownloading.keys.size == 1) {
                Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mImageListAdapter = ImageListAdapter(context!!)
        return inflater.inflate(R.layout.fragment_image_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentProgressBar.visibility = View.VISIBLE
        fragmentProgressBar.isIndeterminate = true
        imageGridRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = mImageListAdapter
        }
        downloadButton.setOnClickListener {
            mDownloadActionsCreator.downloadMultipleImages(context!!, mImagesToDownloadList)
            mAppActionsCreator.enableEditMode(false)
            mImagesToDownloadList.map { image -> image.isDownloading = true }
        }
        mImageListAdapter.setOnClickListener(View.OnClickListener {
            if (context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mAppActionsCreator.checkPermission()
            } else {
                val position = imageGridRecyclerView.getChildAdapterPosition(it)
                val image = mSearchStore.images[position]
                if (mAppStore.editModeEnabled) {
                    if (mImagesToDownloadList.contains(image) && mImagesToDownloadList.size > 1) {
                        mImagesToDownloadList.remove(image)
                        it.alpha = 1f
                    } else {
                        mImagesToDownloadList.add(image)
                        it.alpha = .5f
                    }
                } else {
                    AlertDialog.Builder(context!!)
                            .setTitle(resources.getString(R.string.download_this_image_dialog_title))
                            .setPositiveButton(resources.getString(R.string.download_dialog_positive_button)) { _, _ ->
                                mDownloadActionsCreator.downloadImage(context!!, image)
                                image.isDownloading = true
                                mImageListAdapter.notifyItemChanged(position)
                            }
                            .setNegativeButton(resources.getString(R.string.download_dialog_negative_button)) { _, _ -> }
                            .show()
                }
            }
        })
        mImageListAdapter.setOnLongClickListener(View.OnLongClickListener {
            if (context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mAppActionsCreator.checkPermission()
            } else {
                mAppActionsCreator.enableEditMode(true)
                mImagesToDownloadList.add(mSearchStore.images[imageGridRecyclerView.getChildAdapterPosition(it)])
                it.alpha = .5f
            }
            true
        })
    }

    override fun update(o: Observable?, arg: Any?) {
        val reaction = arg as Reaction
        when (reaction.type) {
            DOWNLOAD_IMAGE ->
                context!!.registerReceiver(mDownloadCompletionReceiver, IntentFilter(ACTION_DOWNLOAD_COMPLETE))
            ENABLE_EDIT_MODE -> {
                if (mAppStore.editModeEnabled) {
                    downloadButton.visibility = View.VISIBLE
                } else {
                    downloadButton.visibility = View.GONE
                    mImagesToDownloadList.clear()
                }
            }
            SEARCH_BY_TERM -> {
                fragmentProgressBar.visibility = View.GONE
                fragmentProgressBar.isIndeterminate = false
                mImageListAdapter.updateImageList(mSearchStore.images)
            }
            NOTIFY_DOWNLOAD_COMPLETE -> {
                mImageListAdapter.updateImageList(mSearchStore.images)
                if (mDownloadStore.imagesDownloading.isEmpty()) {
                    context!!.unregisterReceiver(mDownloadCompletionReceiver)
                }
            }
        }
    }

    companion object {
        val instance: ImageGridFragment by lazy { ImageGridFragment() }
    }
}
