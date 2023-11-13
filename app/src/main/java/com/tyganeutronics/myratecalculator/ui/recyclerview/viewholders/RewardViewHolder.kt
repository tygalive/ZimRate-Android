package com.tyganeutronics.myratecalculator.ui.recyclerview.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.utils.traits.findViewById

class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val title: TextView
        get() = findViewById(android.R.id.text1)

    val description: TextView
        get() = findViewById(android.R.id.text2)

    val balance: TextView
        get() = findViewById(R.id.txt_rewards_balance)

}