package com.jneuberger.imagesearch.action.creator

import android.support.v4.app.Fragment
import com.jneuberger.imagesearch.action.Action
import com.jneuberger.imagesearch.action.ActionKeys.FRAGMENT_KEY
import com.jneuberger.imagesearch.action.AppActions
import com.jneuberger.imagesearch.action.AppActions.Companion.REPLACE_FRAGMENT
import java.util.*

class AppActionsCreator : Observable(), AppActions {
    override fun replaceFragment(fragment: Fragment) = setChanged().run {
        notifyObservers(Action(REPLACE_FRAGMENT, hashMapOf(FRAGMENT_KEY to fragment)))
    }

    companion object {
        val instance: AppActionsCreator by lazy { AppActionsCreator() }
    }
}