package com.jneuberger.imagesearch.network

import com.jneuberger.imagesearch.entity.Image
import java.util.ArrayList

interface AsyncResult {
    fun onProgressUpdate(result: ArrayList<Image>)
    fun onProcessComplete()
}