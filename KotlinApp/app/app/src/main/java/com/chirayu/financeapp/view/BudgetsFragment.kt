package com.chirayu.financeapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chirayu.financeapp.MainActivity
import com.chirayu.financeapp.R
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.databinding.FragmentBudgetsBinding
import com.chirayu.financeapp.model.entities.Budget
import com.chirayu.financeapp.model.entities.mapToRemoteBudget
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.model.taggeditems.TaggedBudget
import com.chirayu.financeapp.util.CustomRecyclerDecorator
import com.chirayu.financeapp.util.RecyclerEditAndDeleteGestures
import com.chirayu.financeapp.util.SettingsUtil
import com.chirayu.financeapp.view.adapters.BudgetsAdapter
import com.chirayu.financeapp.view.viewmodels.BudgetsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class BudgetsFragment : Fragment() {
    private lateinit var viewModel: BudgetsViewModel

    private var _binding: FragmentBudgetsBinding? = null
    private val binding get() = _binding!!

    // Overrides
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetsBinding
            .inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                vm = viewModel
            }

        setupRecyclerViews()
        setupButtons()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[BudgetsViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Methods
    private fun setupRecyclerViews() {
        val today = LocalDate.now()
        val currency = Currencies.from(runBlocking { SettingsUtil.getCurrency().first() })
        val from = getString(R.string.from_display)
        val to = getString(R.string.to_display)

        val activeAdapter = BudgetsAdapter(currency, from, to)
        val pastAdapter = BudgetsAdapter(currency, from, to)

        binding.activeBudgetsRecyclerView.adapter = activeAdapter
        binding.activeBudgetsRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.pastBudgetsRecyclerView.adapter = pastAdapter
        binding.pastBudgetsRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.budgets.observe(viewLifecycleOwner, Observer { budgets ->
            budgets.let {
                activeAdapter.submitList(budgets.filter {
                    !today.isAfter(it.to)
                })
                pastAdapter.submitList(budgets.filter {
                    it.to.isBefore(today)
                })
            }
        })

        setupRecyclerGestures(binding.activeBudgetsRecyclerView)
        setupRecyclerDecorator(binding.activeBudgetsRecyclerView)
        setupRecyclerGestures(binding.pastBudgetsRecyclerView)
        setupRecyclerDecorator(binding.pastBudgetsRecyclerView)
    }

    private fun setupRecyclerGestures(recyclerView: RecyclerView) {
        val gestureCallback = object : RecyclerEditAndDeleteGestures(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val adapter = recyclerView.adapter as BudgetsAdapter
                val budget = adapter.getItemAt(position)

                if (direction == ItemTouchHelper.RIGHT) {
                    (activity as MainActivity).goToEditBudget(budget.budgetId)
                } else if (direction == ItemTouchHelper.LEFT) {
                    onRemoveMovementInvoked(budget)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(gestureCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupRecyclerDecorator(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(
            CustomRecyclerDecorator(
                requireContext()
            )
        )
    }

    private fun setupButtons() {
        binding.addBudgetButton.setOnClickListener {
            (activity as MainActivity).goToNewBudget()
        }

        binding.pastBudgetsCollapseButton.setOnClickListener {
            viewModel.changePastSectionVisibility()
        }
    }

    private fun onRemoveMovementInvoked(taggedBudget: TaggedBudget) {
        val app = requireActivity().application as SaveAppApplication
        val budget = Budget(
            taggedBudget.budgetId,
            taggedBudget.max,
            taggedBudget.used,
            taggedBudget.name,
            taggedBudget.from,
            taggedBudget.to,
            taggedBudget.tagId
        )
        lifecycleScope.launch {
            app.remoteBudgetRepository.delete(budget.mapToRemoteBudget())
        }

        Snackbar.make(binding.budgetsScrollView, R.string.budget_deleted, Snackbar.LENGTH_SHORT)
            .setAction(R.string.undo) {
                lifecycleScope.launch {
                    app.remoteBudgetRepository.insert(budget.mapToRemoteBudget())
                }
            }
            .setAnchorView(requireActivity().findViewById(R.id.bottomAppBar))
            .show()
    }
}
