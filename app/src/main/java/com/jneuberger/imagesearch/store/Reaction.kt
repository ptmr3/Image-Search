package com.jneuberger.imagesearch.store


class Reaction internal constructor(val type: String, val data: HashMap<String, Any>? = null) {
    fun get(tag: String) = data!![tag]
}