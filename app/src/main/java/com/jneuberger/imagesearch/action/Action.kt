package com.jneuberger.imagesearch.action


class Action internal constructor(val type: String, val data: HashMap<String, Any>? = null) {
    fun get(tag: String) = data!![tag]
}