package com.jneuberger.imagesearch.flux.store

import android.support.v4.app.Fragment
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.CANCEL_SEARCH
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*

@RunWith(PowerMockRunner::class)
@PrepareForTest(Action::class, SearchStore::class)
class SearchStoreTest {
    private lateinit var mStore: SearchStore
    private val argumentCaptor = argumentCaptor<Reaction>()
    @Mock lateinit var mAction: Action
    @Mock lateinit var mFragment: Fragment
    @Mock lateinit var mObservable: Observable

    @Before
    fun setup() {
        mStore = spy(SearchStore())
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionCancelSearch() {
        `when`(mAction.type).thenReturn(CANCEL_SEARCH)
        mStore.update(mObservable, mAction)
        verify(mStore, times(0)).notifyObservers(any())
    }
}