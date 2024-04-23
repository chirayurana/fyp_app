package com.chirayu.financeapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chirayu.financeapp.MainActivity
import com.chirayu.financeapp.R
import com.chirayu.financeapp.databinding.FragmentNewIncomeBinding
import com.chirayu.financeapp.databinding.FragmentNewMovementBinding
import com.chirayu.financeapp.model.entities.Tag
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.view.adapters.TagsDropdownAdapter
import com.chirayu.financeapp.view.viewmodels.NewIncomeViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.ZoneId

class NewIncomeFragment : Fragment() {

    private lateinit var viewModel : NewIncomeViewModel

    private var _binding: FragmentNewIncomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[NewIncomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewIncomeBinding
            .inflate(layoutInflater,container,false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                vm = viewModel
            }

        setupUI()
        return binding.root
    }

    //Methods
    private fun setupUI() {
        binding.amountInput.editText?.setOnFocusChangeListener { view, b ->
            viewModel.amountOnFocusChange(view, b)
        }

        binding.saveButton.setOnClickListener { viewModel.insert() }
        binding.cancelButton.setOnClickListener { (activity as MainActivity).goBack() }
        binding.dateInput.editText?.setOnClickListener { showDatePicker() }

        setupTagPicker()
        setupCurrencyPicker()

        viewModel.onAmountChanged = { manageAmountError() }
        viewModel.onDescriptionChanged = { manageDescriptionError() }
        viewModel.validateTag = { manageTagError() }
    }

    private fun setupTagPicker() {
        val tagAutoComplete = binding.tagInput.editText as AutoCompleteTextView
        viewModel.tags.observe(viewLifecycleOwner, Observer {
            it?.let {
                val adapter = TagsDropdownAdapter(
                    binding.tagInput.context,
                    R.layout.tag_dropdown_item,
                    it
                )

                tagAutoComplete.setAdapter(adapter)
                tagAutoComplete.setOnItemClickListener { parent, _, position, _ ->
                    val selection = parent.adapter.getItem(position) as Tag
                    viewModel.setTag(selection)
                }

//                val tagId =
//                    if (viewModel.editingMovement != null) viewModel.editingMovement!!.tagId
//                    else if (viewModel.editingSubscription != null) viewModel.editingSubscription!!.tagId
//                    else 0
//
//                if (tagId != 0) {
//                    val i = it.indexOfFirst { tag -> tag.id == tagId }
//                    tagAutoComplete.setText(it[i].name, false)
//                    viewModel.setTag(it[i])
//                }
            }
        })
    }

    private fun manageAmountError() {
        val amount = viewModel.getAmount().replace(",", ".").toDoubleOrNull()

        binding.amountInput.error =
            if (amount != null && amount > 0.0) null
            else context?.resources?.getString(R.string.amount_error)
    }

    private fun manageDescriptionError() {
        binding.descriptionInput.error =
            if (viewModel.getDescription().isNotBlank()) null
            else context?.resources?.getString(R.string.description_error)
    }

    private fun setupCurrencyPicker() {
        val currencyAutoComplete = binding.currencyInput.editText as AutoCompleteTextView
        viewModel.currencies.observe(viewLifecycleOwner, Observer {
            it?.let {
                val adapter = ArrayAdapter<Currencies>(
                    binding.currencyInput.context,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    it
                )

                currencyAutoComplete.setAdapter(adapter)
                currencyAutoComplete.setOnItemClickListener { parent, _, position, _ ->
                    val selection = parent.adapter.getItem(position) as Currencies
                    viewModel.setCurrency(selection)
                }
                currencyAutoComplete.setText(viewModel.baseCurrency.name, false)
            }
        })
    }

    private fun manageTagError() {
        binding.tagInput.error =
            if (viewModel.getTag() != null) null
            else context?.resources?.getString(R.string.tag_error)
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText(context?.resources?.getString(R.string.choose_date))
            .setSelection(viewModel.getDate().toEpochDay() * NewMovementFragment.MILLISECONDS_IN_DAY)
            .build()

        datePicker.addOnPositiveButtonClickListener { setSelectedDate(datePicker) }
        datePicker.show(childFragmentManager, "movement_date_picker")
    }

    private fun setSelectedDate(datePicker: MaterialDatePicker<Long>) {
        datePicker.selection ?: return

        viewModel.setDate(
            Instant
                .ofEpochMilli(datePicker.selection!!)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        )
    }
}