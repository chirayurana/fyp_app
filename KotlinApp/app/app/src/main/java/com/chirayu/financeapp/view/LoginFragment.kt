package com.chirayu.financeapp.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chirayu.financeapp.MainActivity
import com.chirayu.financeapp.R
import com.chirayu.financeapp.databinding.FragmentLoginBinding
import com.chirayu.financeapp.databinding.FragmentSignupBinding
import com.chirayu.financeapp.view.viewmodels.LoginUIState
import com.chirayu.financeapp.view.viewmodels.LoginViewModel
import com.chirayu.financeapp.view.viewmodels.SignupViewModel
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding
            .inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                vm = viewModel
            }

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.loginButton.setOnClickListener {
            viewModel.login()
        }

        binding.moveToSignupButton.setOnClickListener {
            findNavController().navigate(R.id.navigateToSignup)
        }

        viewModel.loginUIState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is LoginUIState.Error -> {
                    binding.loginUsernameInput.error = "Unable to log in with provided credentials."
                }
                LoginUIState.NotLogged -> {}
                is LoginUIState.Success -> {
                    Log.d("LoginFragment", "token = ${uiState.token}")
                    Intent(requireActivity(), MainActivity::class.java).apply {
                        startActivity(this)
                        requireActivity().finish()
                    }
                }
            }
        }

        viewModel.onUsernameChanged = { managerUsernameError() }
        viewModel.onPasswordChanged = {managePasswordError()}
    }

    private fun managerUsernameError() {
        binding.loginUsernameInput.error =
            if (viewModel.getUsername() != "") null else getString(R.string.username_is_empty)
    }

    private fun managePasswordError() {
        binding.loginUsernamePasswordInput.error = if(viewModel.getPassword() != "") null else getString(
            R.string.password_is_empty
        )
    }

}