package com.jneuberger.imagesearch.flux.action.creator

import android.content.Context
import com.jneuberger.imagesearch.entity.Image
import com.jneuberger.imagesearch.flux.Keys.DOWNLOAD_ID_KEY
import com.jneuberger.imagesearch.flux.Keys.IMAGE_KEY
import com.jneuberger.imagesearch.flux.Keys.IMAGE_LIST_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.DOWNLOAD_IMAGE
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.DOWNLOAD_MULTIPLE_IMAGES
import com.jneuberger.imagesearch.flux.action.DownloadActions.Companion.NOTIFY_DOWNLOAD_COMPLETE
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(AppActionsCreator::class)
class DownloadActionsCreatorTest {
    @Mock private lateinit var mContext: Context
    @Mock private lateinit var mImage: Image
    private lateinit var mActionsCreator: DownloadActionsCreator
    private val argumentCaptor = argumentCaptor<Action>()

    @Before
    fun setup() {
        mActionsCreator = spy(DownloadActionsCreator())
    }

    @Test
    @Throws(Exception::class)
    fun testOnDownloadImage() {
        mActionsCreator.downloadImage(mContext, mImage)
        verify(mActionsCreator).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(DOWNLOAD_IMAGE))
        assertThat(argumentCaptor.firstValue.data!![IMAGE_KEY] as Image, equalTo(mImage))
    }

    @Test
    @Throws(Exception::class)
    fun testOnDownloadMultipleImages() {
        mActionsCreator.downloadMultipleImages(mContext, listOf(mImage, mImage))
        verify(mActionsCreator).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(DOWNLOAD_MULTIPLE_IMAGES))
        assertThat(argumentCaptor.firstValue.data!![IMAGE_LIST_KEY] as List<Image>, equalTo(listOf(mImage, mImage)))
    }

    @Test
    @Throws(Exception::class)
    fun testOnNotifyDownloadComplete() {
        mActionsCreator.notifyDownloadComplete(TEST_DOWNLOAD_ID)
        verify(mActionsCreator).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(NOTIFY_DOWNLOAD_COMPLETE))
        assertThat(argumentCaptor.firstValue.data!![DOWNLOAD_ID_KEY] as Long, equalTo(TEST_DOWNLOAD_ID))
    }

    companion object {
        private const val TEST_DOWNLOAD_ID = 1000L
    }
}