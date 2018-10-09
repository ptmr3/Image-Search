package com.jneuberger.imagesearch.flux.action.creator

import android.support.v4.app.Fragment
import com.jneuberger.imagesearch.flux.Keys.EDIT_MODE_BOOLEAN_KEY
import com.jneuberger.imagesearch.flux.Keys.FRAGMENT_KEY
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.ENABLE_EDIT_MODE
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REMOVE_FRAGMENT
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REPLACE_FRAGMENT
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(AppActionsCreator::class)
class AppActionsCreatorTest {
    @Mock private lateinit var mFragment: Fragment
    private val argumentCaptor = argumentCaptor<Action>()
    private lateinit var mActionsCreator: AppActionsCreator

    @Before
    fun setup() {
        mActionsCreator = spy(AppActionsCreator())
    }

    @Test
    @Throws(Exception::class)
    fun testOnEnableEditMode() {
        mActionsCreator.enableEditMode(false)
        verify(mActionsCreator).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(ENABLE_EDIT_MODE))
        assertThat(argumentCaptor.firstValue.data!![EDIT_MODE_BOOLEAN_KEY] as Boolean, equalTo(false))
    }


    @Test
    @Throws(Exception::class)
    fun testOnRemoveFragment() {
        mActionsCreator.removeFragment(mFragment)
        verify(mActionsCreator).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(REMOVE_FRAGMENT))
        assertThat(argumentCaptor.firstValue.data!![FRAGMENT_KEY] as Fragment, equalTo(mFragment))
    }

    @Test
    @Throws(Exception::class)
    fun testOnReplaceFragment() {
        mActionsCreator.replaceFragment(mFragment)
        verify(mActionsCreator).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(REPLACE_FRAGMENT))
        assertThat(argumentCaptor.firstValue.data!![FRAGMENT_KEY] as Fragment, equalTo(mFragment))
    }
}