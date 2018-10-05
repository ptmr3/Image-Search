package com.jneuberger.imagesearch.action.creator

import com.jneuberger.imagesearch.action.Action
import com.jneuberger.imagesearch.action.ActionKeys.USER_INPUT_KEY
import com.jneuberger.imagesearch.action.SearchActions
import com.jneuberger.imagesearch.action.SearchActions.Companion.SEARCH_BY_TERM
import java.util.*

class SearchActionsCreator : Observable(), SearchActions {
    override fun cancelSearch() = setChanged().run { notifyObservers() }

    override fun searchByTerm(userInput: String) = setChanged().run {
        notifyObservers(Action(SEARCH_BY_TERM, hashMapOf(USER_INPUT_KEY to userInput)))
    }

    companion object {
        val instance : SearchActionsCreator by lazy { SearchActionsCreator() }
    }
}