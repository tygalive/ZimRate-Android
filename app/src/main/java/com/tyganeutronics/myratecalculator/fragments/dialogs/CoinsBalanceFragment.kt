package com.tyganeutronics.myratecalculator.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.database.viewmodels.RewardViewModel
import com.tyganeutronics.myratecalculator.ui.base.BaseExpandedDialogFragment
import com.tyganeutronics.myratecalculator.utils.traits.requireViewById

class CoinsBalanceFragment : BaseExpandedDialogFragment() {

    private lateinit var rewardViewModel: RewardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_coins_balance, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rewardViewModel = ViewModelProvider(this)[RewardViewModel::class.java]
    }

    override fun bindViews() {
        super.bindViews()

        toolBar.apply {
            title = getString(R.string.rewards_coins_title)
            setNavigationIcon(R.drawable.ic_close)
            setNavigationOnClickListener {
                if (isVisible) {
                    dismiss()
                }
            }
        }
    }

    private val toolBar: Toolbar
        get() = requireViewById(R.id.toolbar)

    override fun syncViews() {
        super.syncViews()

        val observer = Observer { balance: Long ->

            requireViewById<AppCompatTextView>(R.id.txt_rewards_status).text = run {
                if (balance > 0) {
                    getString(R.string.rewards_coins_available)
                } else {
                    getString(R.string.rewards_coins_exhausted)
                }
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        rewardViewModel.coins.observe(this, observer)
    }

    companion object {
        const val TAG = "CoinsBalanceFragment"
    }
}