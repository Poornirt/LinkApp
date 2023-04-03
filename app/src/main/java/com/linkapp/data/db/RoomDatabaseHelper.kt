package com.linkapp.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.linkapp.data.dao.LinkDao
import com.linkapp.database.DatabaseHelper
import com.linkapp.database.DatabaseHelper.Companion.DATABASE_NAME
import com.linkapp.jdo.Link

@Database(entities = [Link::class], version = 3)
abstract class RoomDatabaseHelper : RoomDatabase() {

    abstract fun linkDao(): LinkDao

    companion object {
        private var mInstance: RoomDatabaseHelper? = null

        @Synchronized
        fun getInstance(context: Context): RoomDatabaseHelper {
            mInstance = mInstance ?: Room.databaseBuilder(
                context.applicationContext,
                RoomDatabaseHelper::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_2_3)
                .build()
            return mInstance as RoomDatabaseHelper
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
    }


}