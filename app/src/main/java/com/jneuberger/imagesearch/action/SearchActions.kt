package com.jneuberger.imagesearch.action

interface SearchActions {
    fun cancelSearch()
    fun searchByTerm(userInput: String)

    companion object {
        const val CANCEL_SEARCH = "cancelSearch"
        const val SEARCH_BY_TERM = "searchByTerm"
    }
}