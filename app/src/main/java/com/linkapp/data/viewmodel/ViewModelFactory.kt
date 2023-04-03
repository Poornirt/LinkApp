package com.linkapp.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.linkapp.data.db.RoomDatabaseHelper
import com.linkapp.data.repository.LinkRepository
import kotlinx.coroutines.Dispatchers

class ViewModelFactory(var context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LinkViewModel::class.java))
            return LinkViewModel(
                LinkRepository.getInstance(
                    ioDispatcher = Dispatchers.IO,
                    linkDao = RoomDatabaseHelper.getInstance(context).linkDao()
                )
            ) as T
        throw java.lang.IllegalArgumentException("Unknown ViewModel class")
    }

}