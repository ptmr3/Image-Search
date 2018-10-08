package com.jneuberger.imagesearch.network

import com.jneuberger.imagesearch.entity.Image

interface AsyncResult {
    fun onError(throwable: Throwable)
    fun onProgressUpdate(result: ArrayList<Image>)
    fun onProcessComplete(result: ArrayList<Image>?)
}