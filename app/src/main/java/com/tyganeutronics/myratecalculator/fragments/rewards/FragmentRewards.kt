package com.tyganeutronics.myratecalculator.fragments.rewards

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import com.google.firebase.analytics.FirebaseAnalytics
import com.tyganeutronics.myratecalculator.AppZimrate
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.database.contract.RewardContract
import com.tyganeutronics.myratecalculator.database.entities.RewardEntity
import com.tyganeutronics.myratecalculator.interfaces.RewardItemInterface
import com.tyganeutronics.myratecalculator.ui.base.BaseListFragment
import com.tyganeutronics.myratecalculator.ui.recyclerview.adapters.RewardsAdapter
import com.tyganeutronics.myratecalculator.utils.traits.displayBackButton
import com.tyganeutronics.myratecalculator.utils.traits.hideBackButton
import com.tyganeutronics.myratecalculator.utils.traits.setTitle

class FragmentRewards : BaseListFragment(), RewardItemInterface,
    LoaderManager.LoaderCallbacks<List<RewardEntity>> {

    override var items: List<RewardEntity> = emptyList()
    override fun hasItems(): Boolean {
        return items.isNotEmpty()
    }

    override fun syncViews() {
        super.syncViews()

        displayBackButton()

        val type = arguments?.getString(RewardContract.COLUMN_NAME_TYPE, "") ?: ""

        if (type == RewardContract.TYPES.PURCHASE) {
            setTitle(R.string.rewards_purchases_history_title)
        } else {
            setTitle(R.string.rewards_awarded_history_title)
        }
    }

    override fun search(): String? {
        return null
    }

    companion object {
        const val TAG = "FragmentRewards"
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<RewardEntity>> {
        val loader = @SuppressLint("StaticFieldLeak")
        object : AsyncTaskLoader<List<RewardEntity>>(requireContext()) {
            override fun onStartLoading() {
                super.onStartLoading()
                contentLoading()
                forceLoad()
            }

            override fun loadInBackground(): List<RewardEntity> {

                val type = args?.getString(RewardContract.COLUMN_NAME_TYPE, "") ?: ""

                return if (args !== null && type.isNotEmpty()) {
                    AppZimrate.database.rewards().getType(type)
                } else {
                    AppZimrate.database.rewards().getActive()
                }
            }
        }

        return loader
    }

    override fun onLoaderReset(loader: Loader<List<RewardEntity>>) {

    }

    override fun onLoadFinished(loader: Loader<List<RewardEntity>>, data: List<RewardEntity>) {
        items = data
        contentReady()
    }

    override fun onStart() {
        super.onStart()
        LoaderManager.getInstance(this).initLoader(1, arguments, this);
    }

    override fun onStop() {
        super.onStop()
        LoaderManager.getInstance(this).destroyLoader(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(requireContext()).logEvent("view_Rewards_History", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun bindViews() {
        super.bindViews()

        recyclerView.adapter = RewardsAdapter(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        hideBackButton()
    }
}