package com.jneuberger.imagesearch.flux.action.creator

import android.content.Context
import com.jneuberger.imagesearch.flux.action.Action
import com.jneuberger.imagesearch.flux.Keys.USER_INPUT_KEY
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.CANCEL_SEARCH
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.SEARCH_BY_TERM
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
@PrepareForTest(SearchActionsCreator::class)
class SearchActionsCreatorTest {
    @Mock private lateinit var mContext: Context
    private val argumentCaptor = argumentCaptor<Action>()
    private lateinit var mActionsCreator: SearchActionsCreator

    @Before
    fun setup() {
        mActionsCreator = spy(SearchActionsCreator())
    }

    @Test
    @Throws(Exception::class)
    fun testOnCancelSearch() {
        mActionsCreator.cancelSearch()
        verify(mActionsCreator).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(CANCEL_SEARCH))
    }

    @Test
    @Throws(Exception::class)
    fun testOnSearchByTerm() {
        mActionsCreator.searchByTerm(mContext, TEST_SEARCH_TERM)
        verify(mActionsCreator).notifyObservers(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue.type, equalTo(SEARCH_BY_TERM))
        assertThat(argumentCaptor.firstValue.data!![USER_INPUT_KEY] as String, equalTo(TEST_SEARCH_TERM))
    }

    companion object {
        private const val TEST_SEARCH_TERM = "rabbit"
    }
}