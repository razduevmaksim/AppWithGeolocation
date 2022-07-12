package com.example.geolocation.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.geolocation.model.GeolocationModel

@Dao
interface GeolocationDao {

    @Query("SELECT * FROM geolocation_table")
    fun getAll(): LiveData<List<GeolocationModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(geolocationModel: GeolocationModel)

    @Query("DELETE FROM geolocation_table WHERE id=:id")
    fun deleteById(id:Int)

    @Update
    suspend fun update(geolocationModel: GeolocationModel)
}