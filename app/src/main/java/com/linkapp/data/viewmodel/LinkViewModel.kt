package com.linkapp.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkapp.data.repository.LinkRepository
import com.linkapp.data.response.Response
import com.linkapp.jdo.Link
import kotlinx.coroutines.launch

class LinkViewModel(private val mLinkRepository: LinkRepository?) : ViewModel() {

    var getAllLinksLiveData = MutableLiveData<Response<List<Link>>>()
    private var _getAllLinksLiveData = getAllLinksLiveData

    var getSingleLinkLiveData = MutableLiveData<Response<Link>>()
    private var _getSingleLinksLiveData = getSingleLinkLiveData

    var insertLinkLiveData = MutableLiveData<Response<Unit>>()
    private var _insertLinksLiveData = insertLinkLiveData

    var retrieveListLiveData = MutableLiveData<Response<ArrayList<Link>>>()
    private var _retrieveListLiveData = retrieveListLiveData

    fun fetchAllLinks() {
        viewModelScope.launch {
            mLinkRepository?.getAllLinks()?.collect {
                _getAllLinksLiveData.value = it
            }
        }
    }

    fun insertLink(link: Link) {
        viewModelScope.launch {
            mLinkRepository?.insertLink(link)?.collect {
                _insertLinksLiveData.value = it
            }
        }
    }

    fun retrieveListFromMap(list:List<Map<String,String>>) {
        viewModelScope.launch {
            mLinkRepository?.retrievedList(list)?.collect {
                _retrieveListLiveData.value = it
            }
        }
    }

    fun insertAllLink(link: ArrayList<Link>) {
        viewModelScope.launch {
            mLinkRepository?.insertAllLink(link)?.collect {
                _insertLinksLiveData.value = it
            }
        }
    }

    fun fetchSingleLink(linkName:String) {
        viewModelScope.launch {
            mLinkRepository?.getSingleLink(linkName)?.collect {
                _getSingleLinksLiveData.value = it
            }
        }
    }

}