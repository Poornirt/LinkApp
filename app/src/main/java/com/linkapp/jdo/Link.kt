package com.linkapp.jdo

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "LinkDetails")
data class Link(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    var id:Int,
    @ColumnInfo(name = "name")
    @NonNull
    var name:String,
    @ColumnInfo(name = "url")
    @NonNull
    var link_url:String,
    @ColumnInfo(name = "image_url")
    var image_url:String?):Serializable


//
//data class Link(
//    var id:Int,
//    var name:String,
//    var link_url:String,
//    var image_url:String):Serializable