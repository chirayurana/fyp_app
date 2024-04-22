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

sealed interface LoginUIState {
    data object NotLogged : LoginUIState

    data class Error(
        val error : String
    ) : LoginUIState

    data class Success(
        val token : String
    ) : LoginUIState
}

class LoginViewModel(application: Application) : AndroidViewModel(application), Observable {
    private val saveAppApplication = application as SaveAppApplication

    private val userRepository = saveAppApplication.userRepository

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    val loginUIState : MutableLiveData<LoginUIState> = MutableLiveData(LoginUIState.NotLogged)

    private var _username : String = ""
    private var _password : String = ""

    // Bindings
    @Bindable
    fun getUsername() : String {
        return _username
    }

    fun setUsername(value : String) {
        if(value == _username)
            return

        _username = value
        notifyPropertyChanged(BR.username)
    }

    @Bindable
    fun getPassword() : String {
        return _password
    }

    fun setPassword(value : String) {
        if(value == _password)
            return

        _password = value
        notifyPropertyChanged(BR.password)
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
    fun login() {
        viewModelScope.launch {
            Log.d("SignUpViewModel",_username)
            Log.d("SignUpViewModel",_password)

            when(val result = userRepository.login(User(_username,_password))) {
                is NetworkResult.Error -> loginUIState.value = LoginUIState.Error(result.message?: "")
                is NetworkResult.Exception -> loginUIState.value = LoginUIState.Error(result.e.message?: "")
                is NetworkResult.Success -> {
                    saveToken(result.data.token)
                    loginUIState.value = LoginUIState.Success(result.data.token)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            when(val result = userRepository.logout()) {
                is NetworkResult.Error -> Log.d("LoginViewModel","Error to logout ${result.message?: ""}")
                is NetworkResult.Exception -> Log.d("LoginViewModel","Exception to logout ${result.e.message?: ""}")
                is NetworkResult.Success ->  {
                    val sharedPreferencesManager = saveAppApplication.sharedPreferencesManager
                    sharedPreferencesManager.removePreference(
                        SharedPreferencesManager.TOKEN_KEY
                    )
                }
            }
         }
    }

    private fun saveToken(token : String) {
        val sharedPreferencesManager = saveAppApplication.sharedPreferencesManager
        sharedPreferencesManager.addPreference(SharedPreferencesManager.TOKEN_KEY,token)
    }
}