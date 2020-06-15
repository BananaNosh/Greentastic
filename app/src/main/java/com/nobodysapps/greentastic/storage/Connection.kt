package com.nobodysapps.greentastic.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nobodysapps.greentastic.ui.transport.Vehicle

@Entity(tableName = "connections_table", primaryKeys = ["from", "to"])
data class Connection(
    val from: String,
    val to: String,
    val vehicles: List<Vehicle>,
    val updateTime: Long,
    val requested: Boolean
)