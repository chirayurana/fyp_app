package com.chirayu.financeapp.view

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chirayu.financeapp.AuthActivity
import com.chirayu.financeapp.MainActivity
import com.chirayu.financeapp.databinding.FragmentHomeBinding
import com.chirayu.financeapp.view.viewmodels.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Overrides
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding
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
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Methods
    private fun setupUI() {
        viewModel.isUserLoggedIn.observe(viewLifecycleOwner) {
            if(!it) {
                Intent(requireActivity(),AuthActivity::class.java).also { intent->
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        binding.settingsButton.setOnClickListener {
            (activity as MainActivity).goToSettings()
        }

        binding.subscriptionsButton.setOnClickListener {
            (activity as MainActivity).goToSubscriptions()
        }
    }
}
