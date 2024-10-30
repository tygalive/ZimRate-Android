package com.tyganeutronics.myratecalculator.fragments.rewards

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import com.tyganeutronics.myratecalculator.AppZimrate
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.database.entities.SpendEntity
import com.tyganeutronics.myratecalculator.interfaces.SpendItemInterface
import com.tyganeutronics.myratecalculator.ui.base.BaseListFragment
import com.tyganeutronics.myratecalculator.ui.recyclerview.adapters.SpendsAdapter
import com.tyganeutronics.myratecalculator.utils.traits.displayBackButton
import com.tyganeutronics.myratecalculator.utils.traits.hideBackButton
import com.tyganeutronics.myratecalculator.utils.traits.setTitle

class FragmentSpends : BaseListFragment(), SpendItemInterface,
    LoaderManager.LoaderCallbacks<List<SpendEntity>> {

    override var items: List<SpendEntity> = emptyList()

    override fun hasItems(): Boolean {
        return items.isNotEmpty()
    }

    override fun syncViews() {
        super.syncViews()

        displayBackButton()

        setTitle(R.string.rewards_coin_spends_history_title)
    }

    override fun search(): String? {
        return null
    }

    companion object {
        const val TAG = "FragmentSpends"
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<SpendEntity>> {
        val loader = @SuppressLint("StaticFieldLeak")
        object : AsyncTaskLoader<List<SpendEntity>>(requireContext()) {
            override fun onStartLoading() {
                super.onStartLoading()
                contentLoading()
                forceLoad()
            }

            override fun loadInBackground(): List<SpendEntity> {
                return AppZimrate.database.spends().getAll()
            }
        }

        return loader
    }

    override fun onLoaderReset(loader: Loader<List<SpendEntity>>) {

    }

    override fun onLoadFinished(loader: Loader<List<SpendEntity>>, data: List<SpendEntity>) {
        items = data
        contentReady()
    }

    override fun onStart() {
        super.onStart()
        LoaderManager.getInstance(this).initLoader(1, null, this);
    }

    override fun onStop() {
        super.onStop()
        LoaderManager.getInstance(this).destroyLoader(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics.logEvent("view_Purchases_History", null)
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

        recyclerView.adapter = SpendsAdapter(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        hideBackButton()
    }
}