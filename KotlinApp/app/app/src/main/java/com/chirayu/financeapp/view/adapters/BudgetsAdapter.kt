package com.chirayu.financeapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chirayu.financeapp.R
import com.chirayu.financeapp.model.entities.Budget
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.model.taggeditems.TaggedBudget
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar

class BudgetsAdapter(
    private val currency: Currencies,
    private val from: String,
    private val to: String
) :
    ListAdapter<Budget, BudgetsAdapter.BudgetViewHolder>(BudgetsComparator()) {

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        return BudgetViewHolder.create(parent, currency, from, to)
    }

    fun getItemAt(position: Int): Budget {
        return getItem(position)
    }

    class BudgetViewHolder(
        itemView: View,
        private val currency: Currencies,
        private val from: String,
        private val to: String
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val nameItemView = itemView.findViewById<TextView>(R.id.budgetName)
        private val dateFromItemView = itemView.findViewById<TextView>(R.id.budgetDateFrom)
        private val dateToItemView = itemView.findViewById<TextView>(R.id.budgetDateTo)
        private val tagItemView = itemView.findViewById<MaterialButton>(R.id.budgetTagButton)
        private val progressItemView =
            itemView.findViewById<LinearProgressIndicator>(R.id.budgetProgress)
        private val usedItemView = itemView.findViewById<TextView>(R.id.budgetUsed)
        private val maxItemView = itemView.findViewById<TextView>(R.id.budgetMax)

        @SuppressLint("SetTextI18n")
        fun bind(item: Budget) {
            nameItemView.text = item.name
            dateFromItemView.text = String.format(from, item.from.toString())
            dateToItemView.text = String.format(to, item.to.toString())
            tagItemView.setOnClickListener {
                Snackbar.make(
                    itemView.rootView.findViewById(R.id.containerView),
                    item.name,
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(itemView.rootView.findViewById(R.id.bottomAppBar)).show()
            }
            progressItemView.progress = (item.used * 100.0 / item.max).toInt()
            usedItemView.text = "${currency.toSymbol()} ${String.format("%.2f", item.used)}"
            maxItemView.text = "${currency.toSymbol()} ${String.format("%.2f", item.max)}"
        }

        companion object {
            fun create(
                parent: ViewGroup,
                currency: Currencies,
                from: String,
                to: String
            ): BudgetViewHolder {
                val view: View =
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.budget_item, parent, false)

                return BudgetViewHolder(view, currency, from, to)
            }
        }
    }

    class BudgetsComparator : DiffUtil.ItemCallback<Budget>() {
        override fun areContentsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.max == oldItem.max &&
                    oldItem.used == newItem.used &&
                    oldItem.from == newItem.from &&
                    oldItem.to == newItem.to
        }

        override fun areItemsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
