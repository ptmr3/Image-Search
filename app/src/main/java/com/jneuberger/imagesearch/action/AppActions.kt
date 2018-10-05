package com.jneuberger.imagesearch.action

import android.support.v4.app.Fragment

interface AppActions {
    fun replaceFragment(fragment: Fragment)

    companion object {
        const val REPLACE_FRAGMENT = "replaceFragment"
    }
}