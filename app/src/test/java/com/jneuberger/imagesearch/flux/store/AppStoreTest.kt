package com.jneuberger.imagesearch.flux.store

import android.support.v4.app.Fragment
import com.jneuberger.imagesearch.flux.Keys.EDIT_MODE_BOOLEAN_KEY
import com.jneuberger.imagesearch.flux.Keys.FRAGMENT_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.CHECK_PERMISSION
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.ENABLE_EDIT_MODE
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REMOVE_FRAGMENT
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REPLACE_FRAGMENT
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
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*

@RunWith(PowerMockRunner::class)
@PrepareForTest(Action::class, AppStore::class)
class AppStoreTest {
    private lateinit var mStore: AppStore
    private val argumentCaptor = argumentCaptor<Reaction>()
    @Mock lateinit var mAction: Action
    @Mock lateinit var mFragment: Fragment
    @Mock lateinit var mObservable: Observable

    @Before
    fun setup() {
        mStore = spy(AppStore())
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionCheckPermission() {
        `when`(mAction.type).thenReturn(CHECK_PERMISSION)
        mStore.update(mObservable, mAction)
        verify(mStore, times(1)).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(mAction.type))
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionEnableEditMode() {
        `when`(mAction.type).thenReturn(ENABLE_EDIT_MODE)
        `when`(mAction.data).thenReturn(hashMapOf(EDIT_MODE_BOOLEAN_KEY to true))
        mStore.update(mObservable, mAction)
        assertThat(mStore.editModeEnabled, equalTo(true))
        verify(mStore, times(1)).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(mAction.type))
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionRemoveFragment() {
        `when`(mAction.type).thenReturn(REMOVE_FRAGMENT)
        `when`(mAction.data).thenReturn(hashMapOf(FRAGMENT_KEY to mFragment))
        mStore.update(mObservable, mAction)
        verify(mStore, times(1)).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(mAction.type))
        assertThat(argumentCaptor.firstValue.data!![FRAGMENT_KEY] as Fragment, equalTo(mFragment))
    }

    @Test
    @Throws(Exception::class)
    fun testOnActionReplaceFragment() {
        `when`(mAction.type).thenReturn(REPLACE_FRAGMENT)
        `when`(mAction.data).thenReturn(hashMapOf(FRAGMENT_KEY to mFragment))
        mStore.update(mObservable, mAction)
        verify(mStore, times(1)).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(mAction.type))
        assertThat(argumentCaptor.firstValue.data!![FRAGMENT_KEY] as Fragment, equalTo(mFragment))
    }
}