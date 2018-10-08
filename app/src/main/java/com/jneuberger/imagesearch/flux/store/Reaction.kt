package com.jneuberger.imagesearch.flux.store


data class Reaction internal constructor(val type: String, val data: HashMap<String, Any>? = null) {
    fun get(tag: String) = data!![tag]
}