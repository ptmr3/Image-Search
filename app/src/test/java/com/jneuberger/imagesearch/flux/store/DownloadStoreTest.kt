package com.jneuberger.imagesearch.flux.store

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.jneuberger.imagesearch.entity.Image
import com.jneuberger.imagesearch.flux.Keys.CONTEXT_KEY
import com.jneuberger.imagesearch.flux.Keys.DOWNLOAD_ID_KEY
import com.jneuberger.imagesearch.flux.Keys.IMAGE_KEY
import com.jneuberger.imagesearch.flux.Keys.IMAGE_LIST_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.DOWNLOAD_IMAGE
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.DOWNLOAD_MULTIPLE_IMAGES
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.NOTIFY_DOWNLOAD_COMPLETE
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.api.mockito.PowerMockito.whenNew
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File
import java.util.*

@RunWith(PowerMockRunner::class)
@PrepareForTest(Action::class, DownloadManager::class, DownloadManager.Request::class, DownloadStore::class, Environment::class, Image::class, Uri::class)
class DownloadStoreTest {
    private lateinit var mStore: DownloadStore
    private val argumentCaptor = argumentCaptor<Reaction>()
    @Mock private lateinit var mAction: Action
    @Mock private lateinit var mContext: Context
    @Mock private lateinit var mDownloadManager: DownloadManager
    @Mock private lateinit var mDownloadManagerRequest: DownloadManager.Request
    @Mock private lateinit var mFile: File
    @Mock private lateinit var mImage: Image
    @Mock private lateinit var mObservable: Observable
    @Mock private lateinit var mUri: Uri

    @Before
    fun setup() {
        mStore = spy(DownloadStore())
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionDownloadImage() {
        `when`(mAction.type).thenReturn(DOWNLOAD_IMAGE)
        `when`(mAction.data).thenReturn(hashMapOf(CONTEXT_KEY to mContext, IMAGE_KEY to mImage))
        downloadImageCommon(1)
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionDownloadMultipleImages() {
        `when`(mAction.type).thenReturn(DOWNLOAD_MULTIPLE_IMAGES)
        `when`(mAction.data).thenReturn(hashMapOf(CONTEXT_KEY to mContext, IMAGE_LIST_KEY to listOf(mImage, mImage, mImage)))
        downloadImageCommon(3)
    }

    private fun downloadImageCommon(times: Int) {
        mockStatic(DownloadManager::class.java)
        mockStatic(DownloadManager.Request::class.java)
        mockStatic(Environment::class.java)
        mockStatic(Uri::class.java)
        `when`(mImage.downloadLink).thenReturn(TEST_IMAGE_LINK)
        `when`(mImage.user).thenReturn(TEST_IMAGE_UPLOADER)
        `when`(mContext.getSystemService(Context.DOWNLOAD_SERVICE)).thenReturn(mDownloadManager)
        `when`(mDownloadManager.enqueue(mDownloadManagerRequest)).thenReturn(1000, 1001, 1002)
        whenNew(File::class.java).withAnyArguments().thenReturn(mFile)
        PowerMockito.`when`(Environment.getExternalStorageDirectory()).thenReturn(mFile)
        PowerMockito.`when`(Uri.parse(TEST_IMAGE_LINK)).thenReturn(mUri)
        whenNew(DownloadManager.Request::class.java).withAnyArguments().thenReturn(mDownloadManagerRequest)
        mStore.update(mObservable, mAction)
        verify(mStore, times(times)).notifyObservers(argumentCaptor.capture())
        assertThat(mStore.imagesDownloading.size, equalTo(times))
        assertThat(argumentCaptor.firstValue.type, equalTo(DOWNLOAD_IMAGE))
    }

    @Test
    @Throws(Exception::class)
    fun testOnNotifyDownloadComplete() {
        `when`(mAction.type).thenReturn(NOTIFY_DOWNLOAD_COMPLETE)
        `when`(mAction.data).thenReturn(hashMapOf(DOWNLOAD_ID_KEY to mImage))
        `when`(mImage.isDownloading).thenReturn(false)
        `when`(mImage.isDownloaded).thenReturn(true)
        mStore.update(mObservable, mAction)
        verify(mStore, times(1)).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(mAction.type))
        assertThat(mImage.isDownloading, equalTo(false))
        assertThat(mImage.isDownloaded, equalTo(true))
    }

    companion object {
        const val TEST_IMAGE_LINK = "https://test.image.link"
        const val TEST_IMAGE_UPLOADER = "Qui-Gon Jinn"
    }
}