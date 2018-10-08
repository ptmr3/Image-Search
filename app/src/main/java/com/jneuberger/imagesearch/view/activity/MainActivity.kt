package com.jneuberger.imagesearch.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.flux.Keys.FRAGMENT_KEY
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.CHECK_PERMISSION
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.ENABLE_EDIT_MODE
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REMOVE_FRAGMENT
import com.jneuberger.imagesearch.flux.action.AppActions.Companion.REPLACE_FRAGMENT
import com.jneuberger.imagesearch.flux.action.SearchActions.Companion.SEARCH_BY_TERM
import com.jneuberger.imagesearch.flux.action.creator.AppActionsCreator
import com.jneuberger.imagesearch.flux.action.creator.DownloadActionsCreator
import com.jneuberger.imagesearch.flux.action.creator.SearchActionsCreator
import com.jneuberger.imagesearch.flux.store.AppStore
import com.jneuberger.imagesearch.flux.store.DownloadStore
import com.jneuberger.imagesearch.flux.store.Reaction
import com.jneuberger.imagesearch.flux.store.SearchStore
import com.jneuberger.imagesearch.util.EventLogger
import com.jneuberger.imagesearch.view.fragment.ImageGridFragment
import com.jneuberger.imagesearch.view.fragment.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), Observer {
    private var mEventLogger = EventLogger.instance
    private var mAppActionsCreator = AppActionsCreator.instance
    private var mAppStore = AppStore.instance
    private var mDownloadActionsCreator = DownloadActionsCreator.instance
    private var mDownloadStore = DownloadStore.instance
    private var mImageGridFragment = ImageGridFragment.instance
    private var mSearchActionsCreator = SearchActionsCreator.instance
    private var mSearchStore = SearchStore.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        registerObservers()
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.findFragmentById(R.id.container) ?: run {
            mAppActionsCreator.replaceFragment(SearchFragment.instance)
        }
    }

    override fun onBackPressed() {
        mSearchActionsCreator.cancelSearch()
        if (supportFragmentManager.findFragmentById(R.id.container) is SearchFragment) {
            finish()
        } else {
            mAppActionsCreator.replaceFragment(SearchFragment.instance)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSearchActionsCreator.cancelSearch()
        mAppActionsCreator.deleteObservers()
        mAppStore.deleteObservers()
        mDownloadActionsCreator.deleteObservers()
        mDownloadStore.deleteObservers()
        mSearchActionsCreator.deleteObservers()
        mSearchStore.deleteObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (mAppStore.editModeEnabled) {
                    mAppActionsCreator.enableEditMode(false)
                } else {
                    onBackPressed()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@MainActivity, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        val reaction = arg as Reaction
        when (reaction.type) {
            CHECK_PERMISSION -> if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permission_dialog_title))
                        .setMessage(getString(R.string.permission_dialog_description))
                        .setPositiveButton(getString(R.string.permission_dialog_positive_button)) { _, _ ->
                            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                        }
                        .show()
            }
            ENABLE_EDIT_MODE -> {
                if (mAppStore.editModeEnabled) {
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.title = getString(R.string.edit_mode_title)
                } else {
                    supportActionBar?.title = resources.getString(R.string.app_name)
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_material)
                    supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.findFragmentById(R.id.container) !is SearchFragment)
                }
            }
            REMOVE_FRAGMENT -> supportFragmentManager.beginTransaction()
                    .remove(reaction.get(FRAGMENT_KEY) as Fragment).commitAllowingStateLoss()
            REPLACE_FRAGMENT -> {
                val fragment = reaction.get(FRAGMENT_KEY) as Fragment
                supportActionBar?.setDisplayHomeAsUpEnabled(fragment is ImageGridFragment)
                supportActionBar?.title = if (fragment is SearchFragment) {
                    resources.getString(R.string.app_name)
                } else {
                    ""
                }
                supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss()
            }
            SEARCH_BY_TERM -> supportActionBar?.title = mSearchStore.searchTerm
        }
    }

    private fun registerObservers() {
        mAppActionsCreator.apply {
            addObserver(mAppStore)
            addObserver(mEventLogger)
        }
        mAppStore.apply {
            addObserver(this@MainActivity)
            addObserver(mEventLogger)
            addObserver(mImageGridFragment)
        }
        mDownloadActionsCreator.apply {
            addObserver(mDownloadStore)
            addObserver(mEventLogger)
        }
        mDownloadStore.apply {
            addObserver(this@MainActivity)
            addObserver(mEventLogger)
            addObserver(mImageGridFragment)
        }
        mSearchActionsCreator.apply {
            addObserver(mEventLogger)
            addObserver(mSearchStore)
        }
        mSearchStore.apply {
            addObserver(this@MainActivity)
            addObserver(mEventLogger)
            addObserver(mImageGridFragment)
        }
    }
}