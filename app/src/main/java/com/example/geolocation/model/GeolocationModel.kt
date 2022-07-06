package com.example.geolocation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geolocation_table")
class GeolocationModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo
    var title: String= "",

    @ColumnInfo
    var latitude: String = "",

    @ColumnInfo
    var longitude: String = ""
)