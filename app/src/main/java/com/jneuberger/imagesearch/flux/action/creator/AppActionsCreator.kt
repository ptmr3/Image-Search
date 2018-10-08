package com.jneuberger.imagesearch.flux.action.creator

import android.support.v4.app.Fragment
import com.jneuberger.imagesearch.flux.Keys.EDIT_MODE_BOOLEAN_KEY
import com.jneuberger.imagesearch.flux.Keys.FRAGMENT_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.AppActions
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.CHECK_PERMISSION
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.ENABLE_EDIT_MODE
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REMOVE_FRAGMENT
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REPLACE_FRAGMENT
import java.util.*

class AppActionsCreator : Observable(), AppActions {
    override fun checkPermission() = setChanged().run { notifyObservers(Action(CHECK_PERMISSION)) }

    override fun enableEditMode(shouldEnable: Boolean) = setChanged().run {
        notifyObservers(Action(ENABLE_EDIT_MODE, hashMapOf(EDIT_MODE_BOOLEAN_KEY to shouldEnable)))
    }

    override fun removeFragment(fragment: Fragment) = setChanged().run {
        notifyObservers(Action(REMOVE_FRAGMENT, hashMapOf(FRAGMENT_KEY to fragment)))
    }

    override fun replaceFragment(fragment: Fragment) = setChanged().run {
        notifyObservers(Action(REPLACE_FRAGMENT, hashMapOf(FRAGMENT_KEY to fragment)))
    }

    companion object {
        val instance: AppActionsCreator by lazy { AppActionsCreator() }
    }
}