package com.jneuberger.imagesearch.flux.store

import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.CHECK_PERMISSION
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import java.lang.Exception
import java.util.*

class AppStoreTest {
private lateinit var mStore: AppStore
    @Mock lateinit var mObservable: Observable
    @Mock lateinit var mAction: Action

    @Before
    fun setup() {
        mStore = spy(AppStore())
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionCheckPermission() {
        `when`(mAction.type).thenReturn(CHECK_PERMISSION)
        mStore.update(mObservable, mAction)
        verify(mStore, times(1)).notifyObservers(Reaction(mAction.type))
    }
}