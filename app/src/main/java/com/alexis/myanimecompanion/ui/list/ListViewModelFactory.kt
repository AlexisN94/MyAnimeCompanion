package com.alexis.myanimecompanion.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
