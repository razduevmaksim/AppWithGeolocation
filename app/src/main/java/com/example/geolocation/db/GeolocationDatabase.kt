package com.example.geolocation.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.geolocation.db.dao.GeolocationDao
import com.example.geolocation.model.GeolocationModel

@Database(entities = [GeolocationModel::class], version = 5)
abstract class GeolocationDatabase : RoomDatabase() {
    abstract fun getGeolocationDao(): GeolocationDao

    companion object{
        private var database: GeolocationDatabase ?= null

        @Synchronized
        fun getInstance(context: Context):GeolocationDatabase{

            return if (database==null){
                database = Room.databaseBuilder(context,GeolocationDatabase::class.java, "db").build()
                database as GeolocationDatabase
            }
            else{
                database as GeolocationDatabase
            }
        }
    }
}