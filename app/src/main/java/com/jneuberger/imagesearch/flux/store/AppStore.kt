package com.jneuberger.imagesearch.flux.store

import com.jneuberger.imagesearch.flux.Keys.EDIT_MODE_BOOLEAN_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.CHECK_PERMISSION
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.ENABLE_EDIT_MODE
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REMOVE_FRAGMENT
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REPLACE_FRAGMENT
import java.util.*

class AppStore : Observable(), Observer {
    var editModeEnabled: Boolean = false

    override fun update(o: Observable?, arg: Any?) {
        arg ?: run { return }
        val action = arg as Action
        when (action.type) {
            CHECK_PERMISSION -> setChanged().run { notifyObservers(Reaction(action.type)) }
            ENABLE_EDIT_MODE -> {
                editModeEnabled = action.data!![EDIT_MODE_BOOLEAN_KEY] as Boolean
                setChanged().run { notifyObservers(Reaction(action.type)) }
            }
            REMOVE_FRAGMENT, REPLACE_FRAGMENT ->
                setChanged().run { notifyObservers(Reaction(action.type, action.data)) }
        }
    }

    companion object {
        val instance: AppStore by lazy { AppStore() }
    }
}