package com.linkapp.data.repository

import com.linkapp.data.dao.LinkDao
import com.linkapp.data.response.Response
import com.linkapp.jdo.Link
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LinkRepository(private var ioDispatcher: CoroutineDispatcher,private var linkDao: LinkDao) {


    companion object {
        private var mRepository: LinkRepository? = null

        fun getInstance(ioDispatcher: CoroutineDispatcher,linkDao: LinkDao): LinkRepository? {
            synchronized(this) {
                if (mRepository == null) {
                    mRepository =
                        LinkRepository(ioDispatcher,linkDao)
                }
                return@synchronized mRepository
            }
            return mRepository
        }
    }


    suspend fun insertAllLink(link:ArrayList<Link>) = flow {
        try {
            emit(Response.Success(linkDao.insertAll(link)))
        } catch (e: Exception) {
            emit(Response.Error(e.message))
        }
    }.flowOn(ioDispatcher)

    suspend fun insertLink(link:Link) = flow {
        try {
            emit(Response.Success(linkDao.insert(link)))
        } catch (e: Exception) {
            emit(Response.Error(e.message))
        }
    }.flowOn(ioDispatcher)

    suspend fun getAllLinks() = flow {
        try {
            emit(Response.Success(linkDao.getAllLinks()))
        } catch (e: Exception) {
            emit(Response.Error(e.message))
        }
    }.flowOn(ioDispatcher)

    suspend fun getSingleLink(linkName:String) = flow {
        try {
            emit(Response.Success(linkDao.getSingleLink(linkName)))
        } catch (e: Exception) {
            emit(Response.Error(e.message))
        }
    }.flowOn(ioDispatcher)

}