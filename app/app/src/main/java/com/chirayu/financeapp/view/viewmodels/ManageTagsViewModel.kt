package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.model.entities.Tag

class ManageTagsViewModel(application: Application) : AndroidViewModel(application) {
    private val tagRepository = (application as SaveAppApplication).tagRepository

    val tags: LiveData<List<Tag>> = tagRepository.allTags.asLiveData()
}
