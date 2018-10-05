package com.jneuberger.imagesearch.view.fragment

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.action.creator.AppActionsCreator
import com.jneuberger.imagesearch.action.creator.SearchActionsCreator
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private var mAppActionsCreator = AppActionsCreator.instance
    private var mSearchActionsCreator = SearchActionsCreator.instance

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchButton.setOnClickListener {
            val searchTerm = stringToSearchInput.text.toString()
            if (searchTerm.isNotEmpty()) {
                mSearchActionsCreator.searchByTerm(searchTerm)
                mAppActionsCreator.replaceFragment(ImageGridFragment.instance)
            }
        }
    }

    companion object {
        val instance : SearchFragment by lazy { SearchFragment() }
    }
}
