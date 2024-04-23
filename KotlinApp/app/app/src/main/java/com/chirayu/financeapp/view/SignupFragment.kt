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
                is SignupUIState.Error -> {
                    binding.signupUsernameInput.error = "Unable to log in with provided credentials."
                }
                SignupUIState.IsSignedUp -> {
                    Intent(requireActivity(),MainActivity::class.java).apply {
                        startActivity(this)
                        requireActivity().finish()
                    }
                }
                SignupUIState.NotSignedUp -> {}
            }
        }

        viewModel.onUsernameChanged = {managerUsernameError()}
        viewModel.onPasswordChanged = {managePasswordError()}
        viewModel.onConfirmPasswordChanged = {manageConfirmPasswordError()}
    }

    private fun managerUsernameError() {
        binding.signupUsernameInput.error =
            if (viewModel.getUsername() != "") null else getString(R.string.username_is_empty)
    }

    private fun managePasswordError() {
        binding.signupUsernamePasswordInput.error = if(viewModel.getPassword() != "") null else getString(
            R.string.password_is_empty
        )
    }

    private fun manageConfirmPasswordError() {
        binding.signupUsernameConfirmPasswordInput.error = if(viewModel.getConfirmPassword() != "") {
            if(viewModel.getConfirmPassword() != viewModel.getPassword())
                getString(R.string.password_don_t_match)
            else
                null
        } else getString(
            R.string.confirm_password_is_empty
        )
    }

}