package com.jneuberger.imagesearch.flux.action

import android.content.Context

interface SearchActions {
    fun cancelSearch()
    fun searchByTerm(context: Context, userInput: String)

    companion object {
        const val CANCEL_SEARCH = "cancelSearch"
        const val SEARCH_BY_TERM = "searchByTerm"
    }
}