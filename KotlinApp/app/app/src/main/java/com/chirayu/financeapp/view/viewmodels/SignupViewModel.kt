package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chirayu.financeapp.BR
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.models.User
import com.chirayu.financeapp.util.SharedPreferencesManager
import kotlinx.coroutines.launch

sealed interface SignupUIState {
    data object NotSignedUp : SignupUIState
    data object IsSignedUp : SignupUIState

    data class Error(
        val error: String
    ) : SignupUIState
}

class SignupViewModel(application: Application) : AndroidViewModel(application), Observable {
    private val saveAppApplication = application as SaveAppApplication

    private val userRepository = saveAppApplication.userRepository

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    val signupUIState: MutableLiveData<SignupUIState> = MutableLiveData(SignupUIState.NotSignedUp)

    private var _username: String = ""
    private var _password: String = ""
    private var _confirmPassword: String = ""

    // Bindings
    @Bindable
    fun getUsername(): String {
        return _username
    }

    fun setUsername(value: String) {
        if (value == _username)
            return

        _username = value
        notifyPropertyChanged(BR.username)
    }

    @Bindable
    fun getPassword(): String {
        return _password
    }

    fun setPassword(value: String) {
        if (value == _password)
            return

        _password = value
        notifyPropertyChanged(BR.password)
    }

    @Bindable
    fun getConfirmPassword(): String {
        return _confirmPassword
    }

    fun setConfirmPassword(value: String) {
        if (value == _confirmPassword)
            return

        _confirmPassword = value
        notifyPropertyChanged(BR.confirmPassword)
    }


    // Overrides
    override fun addOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback?
    ) {
        callbacks.remove(callback)
    }

    private fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    // Methods
    fun signup() {
        viewModelScope.launch {
            Log.d("SignUpViewModel", _username)
            Log.d("SignUpViewModel", _password)
            Log.d("SignUpViewModel", _confirmPassword)

            when (val result =
                userRepository.signup(User(_username, _password, _confirmPassword))) {
                is NetworkResult.Error -> signupUIState.value =
                    SignupUIState.Error(result.message ?: "")

                is NetworkResult.Exception -> signupUIState.value =
                    SignupUIState.Error(result.e.message ?: "")

                is NetworkResult.Success -> {
                    login()
                }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            when (val result = userRepository.login(User(_username, _password))) {
                is NetworkResult.Error -> signupUIState.value =
                    SignupUIState.Error(result.message ?: "")

                is NetworkResult.Exception -> signupUIState.value =
                    SignupUIState.Error(result.e.message ?: "")

                is NetworkResult.Success -> {
                    saveToken(result.data.token)
                    signupUIState.value = SignupUIState.IsSignedUp
                }
            }
        }
    }

    private fun saveToken(token: String) {
        val sharedPreferencesManager = saveAppApplication.sharedPreferencesManager
        sharedPreferencesManager.addPreference(SharedPreferencesManager.TOKEN_KEY, token)
    }
}