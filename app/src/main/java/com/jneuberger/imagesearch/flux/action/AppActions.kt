package com.jneuberger.imagesearch.flux.action

import android.support.v4.app.Fragment

interface AppActions {
    fun checkPermission()
    fun enableEditMode(shouldEnable: Boolean)
    fun removeFragment(fragment: Fragment)
    fun replaceFragment(fragment: Fragment)

    companion object {
        const val CHECK_PERMISSION = "checkPermission"
        const val ENABLE_EDIT_MODE = "enableEditMode"
        const val REMOVE_FRAGMENT = "removeFragment"
        const val REPLACE_FRAGMENT = "replaceFragment"
    }
}