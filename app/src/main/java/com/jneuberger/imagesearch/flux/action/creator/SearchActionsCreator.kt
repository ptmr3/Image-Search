package com.jneuberger.imagesearch.flux.action.creator

import android.content.Context
import com.jneuberger.imagesearch.flux.Keys.CONTEXT_KEY
import com.jneuberger.imagesearch.flux.Keys.USER_INPUT_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.SearchActions
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.CANCEL_SEARCH
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.SEARCH_BY_TERM
import java.util.*

class SearchActionsCreator : Observable(), SearchActions {
    override fun cancelSearch() = setChanged().run { notifyObservers(Action(CANCEL_SEARCH)) }

    override fun searchByTerm(context: Context, userInput: String) = setChanged().run {
        notifyObservers(Action(SEARCH_BY_TERM, hashMapOf(CONTEXT_KEY to context, USER_INPUT_KEY to userInput)))
    }

    companion object {
        val instance: SearchActionsCreator by lazy { SearchActionsCreator() }
    }
}