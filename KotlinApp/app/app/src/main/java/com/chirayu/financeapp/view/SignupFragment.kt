package com.chirayu.financeapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chirayu.financeapp.MainActivity
import com.chirayu.financeapp.R
import com.chirayu.financeapp.databinding.FragmentSignupBinding
import com.chirayu.financeapp.view.viewmodels.SignupUIState
import com.chirayu.financeapp.view.viewmodels.SignupViewModel

class SignupFragment : Fragment() {

    private lateinit var viewModel : SignupViewModel

    private var _binding : FragmentSignupBinding? = null

    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding
            .inflate(inflater,container,false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                vm = viewModel
            }

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.signupButton.setOnClickListener {
            viewModel.signup()
        }

        binding.moveToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.navigateToLogin)
        }

        viewModel.signupUIState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is SignupUIState.Error -> Log.d("SignupFragment","Error to sign up "+uiState.error)
                SignupUIState.IsSignedUp -> {
                    Intent(requireActivity(),MainActivity::class.java).apply {
                        startActivity(this)
                        requireActivity().finish()
                    }
                }
                SignupUIState.NotSignedUp -> {}
            }
        }
    }


}