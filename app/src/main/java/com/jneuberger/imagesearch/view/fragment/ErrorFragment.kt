package com.jneuberger.imagesearch.view.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.flux.action.creator.AppActionsCreator
import com.jneuberger.imagesearch.flux.action.creator.SearchActionsCreator
import com.jneuberger.imagesearch.flux.store.SearchStore
import com.jneuberger.imagesearch.util.Constants.ERROR_DESCRIPTION
import com.jneuberger.imagesearch.util.Constants.ERROR_IMAGE
import com.jneuberger.imagesearch.util.Constants.ERROR_TITLE
import com.jneuberger.imagesearch.util.Constants.RETRY_BUTTON_ENABLED
import com.jneuberger.imagesearch.util.Constants.WIFI_SETTINGS_ENABLED
import kotlinx.android.synthetic.main.fragment_error.*

class ErrorFragment : Fragment() {
    private var mAppActionsCreator = AppActionsCreator.instance
    private var mSearchActionsCreator = SearchActionsCreator.instance
    private var mSearchStore = SearchStore.instance
    private var mWifiSettingsEnabled: Boolean = false
    private var mRetrySearchEnabled: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            errorImage.setImageDrawable(resources.getDrawable(getInt(ERROR_IMAGE)))
            errorTitle.text = context!!.resources.getString(getInt(ERROR_TITLE))
            errorDescription.text = context!!.resources.getString(getInt(ERROR_DESCRIPTION))
            mWifiSettingsEnabled = getBoolean(WIFI_SETTINGS_ENABLED)
            mRetrySearchEnabled = getBoolean(RETRY_BUTTON_ENABLED)
        }
        retryButton.text = if (mRetrySearchEnabled) {
            resources.getString(R.string.retry_button )
        } else {
            resources.getString(R.string.new_search_button)
        }
        retryButton.setOnClickListener {
            mAppActionsCreator.removeFragment(this)
            if (mRetrySearchEnabled) {
                mAppActionsCreator.replaceFragment(ImageGridFragment.instance)
                mSearchStore.searchTerm?.let { search -> mSearchActionsCreator.searchByTerm(context!!, search) }
            } else {
                mAppActionsCreator.replaceFragment(SearchFragment.instance)
            }
        }
        wifiSettings.visibility = if (mWifiSettingsEnabled) { View.VISIBLE } else { View.GONE }
        wifiSettings.setOnClickListener { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
    }
}
