package com.jneuberger.imagesearch.view.fragment

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.action.ActionKeys
import com.jneuberger.imagesearch.action.AppActions
import com.jneuberger.imagesearch.action.SearchActions.Companion.SEARCH_BY_TERM
import com.jneuberger.imagesearch.action.creator.SearchActionsCreator
import com.jneuberger.imagesearch.store.Reaction
import com.jneuberger.imagesearch.store.SearchStore
import com.jneuberger.imagesearch.view.adapter.ImageListAdapter
import kotlinx.android.synthetic.main.fragment_image_grid.*
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

class ImageGridFragment : Fragment(), Observer {
    private var mSearchActionsCreator = SearchActionsCreator.instance
    private var mSearchStore = SearchStore.instance
    private lateinit var mImageListAdapter: ImageListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mImageListAdapter = ImageListAdapter(context!!)
        return inflater.inflate(R.layout.fragment_image_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageGridRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = mImageListAdapter
        }
        mImageListAdapter.setOnClickListener(View.OnClickListener {
            val position = imageGridRecyclerView.indexOfChild(it)

        })
    }

    override fun update(o: Observable?, arg: Any?) {
        val reaction = arg as Reaction
        when(reaction.type) {
            SEARCH_BY_TERM -> mImageListAdapter.updateImageList(mSearchStore.images)
        }
    }

    companion object {
        val instance : ImageGridFragment by lazy { ImageGridFragment() }
    }
}
