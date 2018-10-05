package com.jneuberger.imagesearch.store

import android.net.Uri
import android.os.AsyncTask
import com.jneuberger.imagesearch.action.Action
import com.jneuberger.imagesearch.action.ActionKeys.USER_INPUT_KEY
import com.jneuberger.imagesearch.action.SearchActions.Companion.CANCEL_SEARCH
import com.jneuberger.imagesearch.action.SearchActions.Companion.SEARCH_BY_TERM
import com.jneuberger.imagesearch.entity.Image
import com.jneuberger.imagesearch.network.AsyncResult
import com.jneuberger.imagesearch.network.SearchImagesRequest
import java.util.*
import kotlin.collections.ArrayList

class SearchStore : Observable(), Observer, AsyncResult {
    private var mSearchRequest: AsyncTask<String?, ArrayList<Image>?, ArrayList<Image>?>? = null
    var images = ArrayList<Image>()

    override fun update(o: Observable?, arg: Any?) {
        val action = arg as Action
        when (action.type) {
            CANCEL_SEARCH -> mSearchRequest?.apply { cancel(true) }
            SEARCH_BY_TERM -> {
                mSearchRequest?.apply { cancel(true) }
                mSearchRequest = SearchImagesRequest(this@SearchStore).execute(action.data!![USER_INPUT_KEY] as String)
            }
        }
    }

    override fun onProgressUpdate(result: ArrayList<Image>) {
        images = result
        setChanged().run{ notifyObservers(Reaction(SEARCH_BY_TERM)) }
    }

    override fun onProcessComplete() {

    }

    companion object {
        val instance : SearchStore by lazy { SearchStore() }
    }
}