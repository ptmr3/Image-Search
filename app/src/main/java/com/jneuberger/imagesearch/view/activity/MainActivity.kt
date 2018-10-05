package com.jneuberger.imagesearch.view.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.jneuberger.imagesearch.EventLogger
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.action.Action
import com.jneuberger.imagesearch.action.ActionKeys.FRAGMENT_KEY
import com.jneuberger.imagesearch.action.AppActions.Companion.REPLACE_FRAGMENT
import com.jneuberger.imagesearch.action.creator.AppActionsCreator
import com.jneuberger.imagesearch.action.creator.SearchActionsCreator
import com.jneuberger.imagesearch.store.AppStore
import com.jneuberger.imagesearch.store.Reaction
import com.jneuberger.imagesearch.store.SearchStore
import com.jneuberger.imagesearch.view.fragment.ImageGridFragment
import com.jneuberger.imagesearch.view.fragment.SearchFragment

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), Observer {
    private var mEventLogger = EventLogger.instance
    private var mAppActionsCreator = AppActionsCreator.instance
    private var mAppStore = AppStore.instance
    private var mImageGridFragment = ImageGridFragment.instance
    private var mSearchActionsCreator = SearchActionsCreator.instance
    private var mSearchStore = SearchStore.instance

    init {
        // Setting up Flux pattern observers
        mAppActionsCreator.apply {
            addObserver(mAppStore)
            addObserver(mEventLogger)
        }
        mAppStore.apply {
            addObserver(this@MainActivity)
            addObserver(mEventLogger)
        }
        mSearchActionsCreator.apply {
            addObserver(mSearchStore)
            addObserver(mEventLogger)
        }
        mSearchStore.apply {
            addObserver(this@MainActivity)
            addObserver(mEventLogger)
            addObserver(mImageGridFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.findFragmentById(R.id.container) ?:run {
            mAppActionsCreator.replaceFragment(SearchFragment.instance)
        }
    }

    override fun onBackPressed() {
        mSearchActionsCreator.cancelSearch()
        if (supportFragmentManager.findFragmentById(R.id.container) is ImageGridFragment) {
            mAppActionsCreator.replaceFragment(SearchFragment.instance)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        val reaction = arg as Reaction
        when(reaction.type) {
            REPLACE_FRAGMENT -> {
                val fragment = reaction.get(FRAGMENT_KEY)  as Fragment
                supportActionBar?.setDisplayHomeAsUpEnabled(fragment is ImageGridFragment)
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit()
            }
        }
    }
}
