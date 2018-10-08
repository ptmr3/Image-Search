package com.jneuberger.imagesearch.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.flux.action.creator.AppActionsCreator
import com.jneuberger.imagesearch.flux.action.creator.SearchActionsCreator
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
            stringToSearchInput.onEditorAction(EditorInfo.IME_ACTION_DONE)
                val searchTerm = stringToSearchInput.text.toString()
                if (searchTerm.isEmpty()) {
                    stringToSearchInput.apply {
                        error = context.getString(R.string.please_enter_text)
                        text?.clear()
                    }
                } else {
                    stringToSearchInput.text?.clear()
                    mAppActionsCreator.replaceFragment(ImageGridFragment.instance)
                    mSearchActionsCreator.searchByTerm(context!! ,searchTerm)
                }
        }
    }

    companion object {
        val instance : SearchFragment by lazy { SearchFragment() }
    }
}
