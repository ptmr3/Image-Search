package com.jneuberger.imagesearch.network

import android.graphics.Bitmap
import android.os.AsyncTask
import com.jneuberger.imagesearch.entity.Image
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import android.graphics.BitmapFactory
import android.net.Uri
import com.jneuberger.imagesearch.action.ActionKeys
import com.jneuberger.imagesearch.store.SearchStore
import java.net.CacheResponse
import kotlin.collections.ArrayList

class SearchImagesRequest(private val mAsyncResultListener: AsyncResult): AsyncTask<String, ArrayList<Image>, ArrayList<Image>?>() {
private val mImageList = ArrayList<Image>()

    override fun doInBackground(vararg params: String): ArrayList<Image>? {
        val imageSearchUrl = Uri.parse(UNSPLASH_URL).buildUpon()
                .appendQueryParameter(CLIENT_ID, TOKEN)
                .appendQueryParameter(QUERY, params[0])
                .appendQueryParameter("per_page", "30")
        try {
            val jsonResponse = request(URL(imageSearchUrl.build().toString()))
            val pages = JSONObject(jsonResponse).getString("total_pages").toInt()
            if (pages > 1) {
                for (page in 0 until pages) {
                    buildImageList(request(URL(imageSearchUrl.appendQueryParameter("page", page.toString()).build().toString())))
                }
            } else {
                buildImageList(jsonResponse)
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        return mImageList
    }

    override fun onProgressUpdate(vararg values: ArrayList<Image>) {
        super.onProgressUpdate(*values)
        mAsyncResultListener.onProgressUpdate(values[0])
    }

    override fun onPostExecute(result: ArrayList<Image>?) {
        super.onPostExecute(result)
        mAsyncResultListener.onProcessComplete()
    }

    private fun request(url: URL): String {
        val connection = url.openConnection() as HttpURLConnection
        connection.apply { requestMethod = GET }.connect()
        val inputStreamReader = InputStreamReader(connection.inputStream)
        val reader = BufferedReader(inputStreamReader)
        val stringBuilder = StringBuilder()
        var inputLine = reader.readLine()
        while ((inputLine) != null) {
            stringBuilder.append(inputLine)
            inputLine = reader.readLine()
        }
        reader.close()
        inputStreamReader.close()
        return stringBuilder.toString()
    }

    private fun buildImageList(jsonResponse: String) {
        var description:String? = null
        var small:String? = null
        var smallImage: Bitmap? = null
        var userFullName:String? = null
        var downloadLink:String? = null
        val results = JSONObject(jsonResponse).getJSONArray("results")
        for (imageItem in 0 until results.length()) {
            results.getJSONObject(imageItem).apply {
                description = getString("description")
                getJSONObject("urls").apply { small = getString("small") }
                smallImage = BitmapFactory.decodeStream(URL(Uri.parse(small).buildUpon()
                        .appendQueryParameter(CLIENT_ID, TOKEN)
                        .build().toString()).openConnection().getInputStream())
                getJSONObject("links").apply { downloadLink = getString("download") }
                getJSONObject("user").apply { userFullName = getString("name") }
            }
            mImageList.add(Image(smallImage, description, userFullName, downloadLink))
            publishProgress(mImageList)
        }
    }

    companion object {
        private const val GET = "GET"
        private const val CLIENT_ID = "client_id"
        private const val QUERY = "query"
        private const val TOKEN = "7c08f9cb51eea74a997b5590cd9b6c0d9806b49de9680da104d99fb2b1f33a63"
        private const val UNSPLASH_URL = "https://api.unsplash.com/search/photos"
    }
}