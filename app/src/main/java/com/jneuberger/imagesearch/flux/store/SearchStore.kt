package com.jneuberger.imagesearch.flux.store

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.AsyncTask.*
import android.os.Bundle
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.entity.Image
import com.jneuberger.imagesearch.flux.Keys.CONTEXT_KEY
import com.jneuberger.imagesearch.flux.Keys.FRAGMENT_KEY
import com.jneuberger.imagesearch.flux.Keys.USER_INPUT_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REPLACE_FRAGMENT
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.CANCEL_SEARCH
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.SEARCH_BY_TERM
import com.jneuberger.imagesearch.network.AsyncResult
import com.jneuberger.imagesearch.network.SearchImagesRequest
import com.jneuberger.imagesearch.util.Constants.ERROR_DESCRIPTION
import com.jneuberger.imagesearch.util.Constants.ERROR_IMAGE
import com.jneuberger.imagesearch.util.Constants.ERROR_TITLE
import com.jneuberger.imagesearch.util.Constants.RETRY_BUTTON_ENABLED
import com.jneuberger.imagesearch.util.Constants.WIFI_SETTINGS_ENABLED
import com.jneuberger.imagesearch.view.fragment.ErrorFragment
import java.util.*
import kotlin.collections.ArrayList

class SearchStore : Observable(), Observer, AsyncResult {
    private var mSearchRequest: AsyncTask<String?, ArrayList<Image>?, ArrayList<Image>?>? = null
    var searchTerm: String? = null

    var images = ArrayList<Image>()
    override fun update(o: Observable?, arg: Any?) {
        arg ?: run { return }
        val action: Action? = arg as Action
        when (action?.type) {
            CANCEL_SEARCH -> mSearchRequest?.apply { cancel(false) }
            SEARCH_BY_TERM -> {
                mSearchRequest?.apply { cancel(false) }
                searchTerm = action.data!![USER_INPUT_KEY] as String
                val networkInfo = ((action.data[CONTEXT_KEY] as Context)
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
                if (networkInfo == null || !networkInfo.isConnected) {
                    showErrorFragment(R.drawable.no_internet_image, R.string.no_internet_title,
                            R.string.no_internet_description, true, true)
                } else {
                    mSearchRequest = SearchImagesRequest(this@SearchStore).executeOnExecutor(THREAD_POOL_EXECUTOR, searchTerm)
                }
            }
        }
    }

    override fun onError(throwable: Throwable) {
        showErrorFragment(R.drawable.error_image, R.string.unknown_error_title,
                R.string.unknown_error_description, true, false)
    }

    override fun onProgressUpdate(result: ArrayList<Image>) {
        images = result
        if (images.size < 20) {
            setChanged().run { notifyObservers(Reaction(SEARCH_BY_TERM)) }
        } else if (images.size % 10 == 0) {
            setChanged().run { notifyObservers(Reaction(SEARCH_BY_TERM)) }
        }
    }

    override fun onProcessComplete(result: ArrayList<Image>?) {
        if (result == null || result.isEmpty()) {
            showErrorFragment(R.drawable.error_image, R.string.no_results_title,
                    R.string.no_results_description, false, false)
        } else {
            setChanged().run { notifyObservers(Reaction(SEARCH_BY_TERM)) }
        }
    }

    private fun showErrorFragment(errorImage: Int, errorTitle: Int, errorDescription: Int, retryEnabled: Boolean, wifiEnabled: Boolean) {
        val bundle = Bundle().apply {
            putInt(ERROR_IMAGE, errorImage)
            putInt(ERROR_TITLE, errorTitle)
            putInt(ERROR_DESCRIPTION, errorDescription)
            putBoolean(RETRY_BUTTON_ENABLED, retryEnabled)
            putBoolean(WIFI_SETTINGS_ENABLED, wifiEnabled)
        }
        val errorFragment = ErrorFragment().apply { arguments = bundle }
        setChanged().run { notifyObservers(Reaction(REPLACE_FRAGMENT, hashMapOf(FRAGMENT_KEY to errorFragment))) }
    }

    companion object {
        val instance: SearchStore by lazy { SearchStore() }
    }
}