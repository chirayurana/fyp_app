package com.chirayu.financeapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.chirayu.financeapp.databinding.ActivityAuthBinding
import com.chirayu.financeapp.util.SharedPreferencesManager

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(isUserLoggedIn()) {
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }

    }

    private fun isUserLoggedIn() : Boolean {
        val sharedPreferencesManager = SharedPreferencesManager(this)
        val token = sharedPreferencesManager.getPreference(SharedPreferencesManager.TOKEN_KEY)
        return token != null
    }
}