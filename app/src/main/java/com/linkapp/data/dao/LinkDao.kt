package com.linkapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linkapp.jdo.Link
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(link: Link)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(link: ArrayList<Link>)

    @Query("select * from LinkDetails")
    fun getAllLinks() : List<Link>

    @Query("select * from LinkDetails where name=:name")
    fun getSingleLink(name:String) : Link
}