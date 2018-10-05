package com.jneuberger.imagesearch.store

import com.jneuberger.imagesearch.action.Action
import com.jneuberger.imagesearch.action.AppActions.Companion.REPLACE_FRAGMENT
import java.util.*

class AppStore : Observable(), Observer {

    override fun update(o: Observable?, arg: Any?) {
        val action = arg as Action
        when (action.type) {
            REPLACE_FRAGMENT -> {
                setChanged().run { notifyObservers(Reaction(action.type, action.data)) }
            }
        }
    }

    companion object {
        val instance: AppStore by lazy { AppStore() }
    }
}