package com.example.geolocation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geolocation_table")
class GeolocationModel(title: String, latitude: String, longitude: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo
    var title: String= title

    @ColumnInfo
    var latitude: String = latitude

    @ColumnInfo
    var longtitude: String = longitude
}