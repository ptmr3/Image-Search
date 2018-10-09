package com.jneuberger.imagesearch.flux.store

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.os.Bundle
import android.support.v4.app.Fragment
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.entity.Image
import com.jneuberger.imagesearch.flux.Keys.CONTEXT_KEY
import com.jneuberger.imagesearch.flux.Keys.FRAGMENT_KEY
import com.jneuberger.imagesearch.flux.Keys.USER_INPUT_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REPLACE_FRAGMENT
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.CANCEL_SEARCH
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.SEARCH_BY_TERM
import com.jneuberger.imagesearch.network.SearchImagesRequest
import com.jneuberger.imagesearch.view.fragment.ErrorFragment
import com.nhaarman.mockito_kotlin.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.whenNew
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*

@RunWith(PowerMockRunner::class)
@PrepareForTest(Action::class, Image::class, SearchImagesRequest::class, SearchStore::class)
class SearchStoreTest {
    private lateinit var mStore: SearchStore
    private val argumentCaptor = argumentCaptor<Reaction>()
    @Mock private lateinit var mAction: Action
    @Mock private lateinit var mAsyncTask: AsyncTask<String?, ArrayList<Image>?, ArrayList<Image>?>
    @Mock private lateinit var mBundle: Bundle
    @Mock private lateinit var mConnectivityManager: ConnectivityManager
    @Mock private lateinit var mContext: Context
    @Mock private lateinit var mNetworkInfo: NetworkInfo
    @Mock private lateinit var mObservable: Observable
    @Mock private lateinit var mResultImageArrayList: ArrayList<Image>
    @Mock private lateinit var mSearchRequest: SearchImagesRequest

    @Before
    fun setup() {
        mStore = spy(SearchStore())
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionCancelSearchWithActiveSearch() {
        setMockSearchRequest()
        `when`(mAction.type).thenReturn(CANCEL_SEARCH)
        mStore.update(mObservable, mAction)
        verify(mStore, times(1)).update(any(), any())
        verify(mAsyncTask, times(1)).cancel(false)
        verifyNoMoreInteractions(mStore)
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionCancelSearchWithNoSearchActive() {
        `when`(mAction.type).thenReturn(CANCEL_SEARCH)
        mStore.update(mObservable, mAction)
        verify(mStore, times(1)).update(any(), any())
        verifyNoMoreInteractions(mStore)
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionSearchByTermWithInternet() {
        setMockSearchRequest()
        searchByTermCommon(true, 1)
        PowerMockito.verifyPrivate(mStore, times(0)).invoke(METHOD_SHOW_ERROR_FRAGMENT,
                R.drawable.no_internet_image, R.string.no_internet_title, R.string.no_internet_description, true, true)
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionSearchByTermWithNoInternet() {
        setMockSearchRequest()
        searchByTermCommon(false, 0)
        verify(mStore, times(1)).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(REPLACE_FRAGMENT))
        assert(argumentCaptor.firstValue.data!![FRAGMENT_KEY] as Fragment is ErrorFragment)
        PowerMockito.verifyPrivate(mStore, times(1)).invoke(METHOD_SHOW_ERROR_FRAGMENT,
                R.drawable.no_internet_image, R.string.no_internet_title, R.string.no_internet_description, true, true)
    }

    @Test
    @Throws(Exception::class)
    fun testOnAsyncResultError() {
        whenNew(Bundle::class.java).withNoArguments().thenReturn(mBundle)
        mStore.onError(Exception())
        verify(mStore, times(1)).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(REPLACE_FRAGMENT))
        assert(argumentCaptor.firstValue.data!![FRAGMENT_KEY] as Fragment is ErrorFragment)
        PowerMockito.verifyPrivate(mStore, times(1)).invoke(METHOD_SHOW_ERROR_FRAGMENT,
                R.drawable.error_image, R.string.unknown_error_title, R.string.unknown_error_description, true, false)
    }

    @Test
    @Throws(Exception::class)
    fun testOnAsyncResultProgressUpdateWith8ResultsShouldNotify() {
        onProgressUpdateCommon(8, 1)
    }

    @Test
    @Throws(Exception::class)
    fun testOnAsyncResultProgressUpdateWith23ResultsShouldNotNotify() {
        onProgressUpdateCommon(23, 0)
    }

    @Test
    @Throws(Exception::class)
    fun testOnAsyncResultProgressUpdateWith50ResultsShouldNotify() {
        onProgressUpdateCommon(50, 1)
    }

    @Test
    @Throws(Exception::class)
    fun testOnAsyncResultProcessCompleteWithNoResults() {
        onProcessCompleteCommon(true, 1)
    }

    @Test
    @Throws(Exception::class)
    fun testOnAsyncResultProcessCompleteWithResults() {
        onProcessCompleteCommon(false, 1)
    }

    private fun onProcessCompleteCommon(isListEmpty: Boolean, times: Int) {
        whenNew(Bundle::class.java).withNoArguments().thenReturn(mBundle)
        `when`(mResultImageArrayList.isEmpty()).thenReturn(isListEmpty)
        mStore.onProcessComplete(mResultImageArrayList)
        verify(mStore, times(times)).notifyObservers(argumentCaptor.capture())
        if (isListEmpty) {
            assertThat(argumentCaptor.firstValue.type, equalTo(REPLACE_FRAGMENT))
            assert(argumentCaptor.firstValue.data!![FRAGMENT_KEY] as Fragment is ErrorFragment)
        } else {
            assertThat(argumentCaptor.firstValue.type, equalTo(SEARCH_BY_TERM))
        }
        PowerMockito.verifyPrivate(mStore, times(times)).invoke(METHOD_SHOW_ERROR_FRAGMENT,
                R.drawable.error_image, R.string.no_results_title, R.string.no_results_description, false, false)
    }

    private fun onProgressUpdateCommon(listSize: Int, times: Int) {
        `when`(mResultImageArrayList.size).thenReturn(listSize)
        mStore.onProgressUpdate(mResultImageArrayList)
        verify(mStore, times(times)).notifyObservers(argumentCaptor.capture())
        if (times > 0) {
            assertThat(argumentCaptor.firstValue.type, equalTo(SEARCH_BY_TERM))
        }
    }

    private fun searchByTermCommon(isConnected: Boolean, times: Int) {
        `when`(mAction.type).thenReturn(SEARCH_BY_TERM)
        `when`(mAction.data).thenReturn(hashMapOf(CONTEXT_KEY to mContext, USER_INPUT_KEY to TEST_SEARCH_TERM))
        `when`(mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mConnectivityManager)
        `when`(mConnectivityManager.activeNetworkInfo).thenReturn(mNetworkInfo)
        `when`(mNetworkInfo.isConnected).thenReturn(isConnected)
        whenNew(SearchImagesRequest::class.java).withAnyArguments().thenReturn(mSearchRequest)
        whenNew(Bundle::class.java).withNoArguments().thenReturn(mBundle)
        mStore.update(mObservable, mAction)
        verify(mAsyncTask, times(1)).cancel(false)
        verify(mSearchRequest, times(times)).executeOnExecutor(THREAD_POOL_EXECUTOR, TEST_SEARCH_TERM)
    }

    private fun setMockSearchRequest() {
        SearchStore::class.java.getDeclaredField(FIELD_SEARCH_REQUEST).apply {
            isAccessible = true
            set(mStore, mAsyncTask)
        }
    }

    companion object {
        private const val FIELD_SEARCH_REQUEST = "mSearchRequest"
        private const val METHOD_SHOW_ERROR_FRAGMENT = "showErrorFragment"
        private const val TEST_SEARCH_TERM = "rabbit"
    }
}