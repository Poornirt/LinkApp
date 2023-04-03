package com.linkapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.linkapp.jdo.Link

class DatabaseHelper(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {


    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "LinkAppDB.db"
        val TABLE_NAME = "LinkDetails"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_LINK_URl = "url"
        val COLUMN_IMAGE_LINK_URl = "image_url"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NAME( $COLUMN_ID INTEGER  primary key not null, $COLUMN_NAME TEXT not null, $COLUMN_LINK_URl TEXT not null, $COLUMN_IMAGE_LINK_URl TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    fun addLinkDetails(pLink: ArrayList<Link>) {
        val values = ContentValues()
        val db = this.writableDatabase
        pLink.forEach {
            values.put(COLUMN_NAME, it.name)
            values.put(COLUMN_LINK_URl, it.link_url)
            values.put(COLUMN_IMAGE_LINK_URl, it.image_url)
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }


    fun getAllLink(): ArrayList<Link> {
        val list = ArrayList<Link>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.count != 0) {
            cursor.moveToFirst()
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val url = cursor.getString(cursor.getColumnIndex(COLUMN_LINK_URl))
                val image_url = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_LINK_URl))
                list.add(Link(id = id,name = name, link_url =  url, image_url = image_url))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}