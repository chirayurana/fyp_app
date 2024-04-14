package com.chirayu.financeapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chirayu.financeapp.MainActivity
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.databinding.FragmentSettingsBinding
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.util.ImportExportUtil
import com.chirayu.financeapp.view.viewmodels.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SettingsFragment : Fragment() {
    private lateinit var viewModel: SettingsViewModel

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Overrides
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding
            .inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                vm = viewModel
            }

        setupUI()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    // Methods
    private fun setupUI() {
        setupCurrencyPicker()
        setupButtons()
    }

    private fun setupCurrencyPicker() {
        val currencyAutoComplete = binding.defaultCurrencyPicker.editText as AutoCompleteTextView
        viewModel.currencies.observe(viewLifecycleOwner, Observer {
            it?.let {
                val adapter = ArrayAdapter(
                    binding.defaultCurrencyPicker.context,
                    R.layout.support_simple_spinner_dropdown_item,
                    it
                )

                currencyAutoComplete.setAdapter(adapter)
                currencyAutoComplete.setOnItemClickListener { parent, _, position, _ ->
                    val activity = requireActivity()
                    MaterialAlertDialogBuilder(activity)
                        .setIcon(com.chirayu.financeapp.R.drawable.baseline_warning_amber_24)
                        .setTitle(activity.getString(com.chirayu.financeapp.R.string.warning))
                        .setMessage(activity.getString(com.chirayu.financeapp.R.string.change_default_currency_alert_message))
                        .setNegativeButton(activity.getString(com.chirayu.financeapp.R.string.cancel)) { _, _ ->
                            val currency =
                                Currencies.from(runBlocking { viewModel.defaultCurrencyId.first() })

                            currencyAutoComplete.setText(currency.name, false)
                            currencyAutoComplete.clearFocus()
                        }
                        .setPositiveButton(activity.getString(com.chirayu.financeapp.R.string.dialog_continue)) { _, _ ->
                            val selection = parent.adapter.getItem(position) as Currencies
                            (requireActivity() as MainActivity).updateAllToNewCurrency(selection)
                        }
                        .show()
                }

                val currency = Currencies.from(runBlocking { viewModel.defaultCurrencyId.first() })
                currencyAutoComplete.setText(currency.name, false)
            }
        })
    }

    private fun setupButtons() {
        val act = (requireActivity().application as SaveAppApplication)
            .getCurrentActivity()!! as MainActivity

        setupMovementsButtons(act)
        setupSubscriptionsButtons(act)
        setupBudgetsButtons(act)

        binding.manageTagsButton.setOnClickListener { act.goToManageTags() }
    }

    private fun setupMovementsButtons(act: MainActivity) {
        binding.templateMovementsButton.setOnClickListener {
            ImportExportUtil.createExportFile(ImportExportUtil.CREATE_MOVEMENTS_TEMPLATE, act)
        }
        binding.importMovementsButton.setOnClickListener {
            ImportExportUtil.getFromFile(ImportExportUtil.OPEN_MOVEMENTS_FILE, act)
        }
        binding.exportMovementsButton.setOnClickListener {
            ImportExportUtil.createExportFile(ImportExportUtil.CREATE_MOVEMENTS_FILE, act)
        }
    }

    private fun setupSubscriptionsButtons(act: MainActivity) {
        binding.templateSubscriptionsButton.setOnClickListener {
            ImportExportUtil.createExportFile(ImportExportUtil.CREATE_SUBSCRIPTIONS_TEMPLATE, act)
        }
        binding.importSubscriptionsButton.setOnClickListener {
            ImportExportUtil.getFromFile(ImportExportUtil.OPEN_SUBSCRIPTIONS_FILE, act)
        }
        binding.exportSubscriptionsButton.setOnClickListener {
            ImportExportUtil.createExportFile(ImportExportUtil.CREATE_SUBSCRIPTIONS_FILE, act)
        }
    }

    private fun setupBudgetsButtons(act: MainActivity) {
        binding.templateBudgetsButton.setOnClickListener {
            ImportExportUtil.createExportFile(ImportExportUtil.CREATE_BUDGETS_TEMPLATE, act)
        }
        binding.importBudgetsButton.setOnClickListener {
            ImportExportUtil.getFromFile(ImportExportUtil.OPEN_BUDGETS_FILE, act)
        }
        binding.exportBudgetsButton.setOnClickListener {
            ImportExportUtil.createExportFile(ImportExportUtil.CREATE_BUDGETS_FILE, act)
        }
    }
}
