package com.jneuberger.imagesearch.flux.action


data class Action internal constructor(val type: String, val data: HashMap<String, Any>? = null) {
    fun get(tag: String) = data!![tag]
}