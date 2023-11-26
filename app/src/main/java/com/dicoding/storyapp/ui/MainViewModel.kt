package com.dicoding.storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.dicoding.storyapp.data.MainRepository
import com.dicoding.storyapp.remote.response.ListStoryItem

class MainViewModel(repository: MainRepository) : ViewModel() {
    val stories: LiveData<PagingData<ListStoryItem>> = repository.getStoriesPagingData()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
}

class MainViewModelFactory(private val repository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}






