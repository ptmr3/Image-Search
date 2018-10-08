package com.jneuberger.imagesearch.util

import android.util.Log
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.store.Reaction
import java.util.*

class EventLogger : Observer {
    override fun update(o: Observable?, arg: Any?) {
        val argString: String = when (arg) {
            is Action -> arg.type
            is Reaction -> arg.type
            else -> arg.toString()
        }
        Log.i(this.javaClass.simpleName, "Class: ${o?.javaClass?.simpleName}, Args: $argString")
    }

    companion object {
        val instance: EventLogger by lazy { EventLogger() }
    }
}