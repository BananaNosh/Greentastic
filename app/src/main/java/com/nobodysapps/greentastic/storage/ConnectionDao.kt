package com.nobodysapps.greentastic.storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.*
import com.nobodysapps.greentastic.ui.transport.Vehicle

@Dao
abstract class ConnectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(connection: Connection)

    @Query("SELECT vehicles FROM connections_table WHERE requested=1")
    protected abstract fun load(): LiveData<Converters.VehiclesWrapper?>

    fun loadRequestedVehicles(): LiveData<List<Vehicle>> {
        return load().map { it?.vehicles?.sortedByDescending { vehicle -> vehicle.total.score } ?: emptyList() }
    }

    @Query("UPDATE connections_table SET requested=1 WHERE `from`=:from AND `to`=:to")
    abstract suspend fun setRequestedIfStored(from: String, to: String): Int

    @Query("UPDATE connections_table SET requested=0")
    abstract suspend fun resetRequested(): Int

//    @Update(entity = Connection::class)
//    abstract suspend fun unrequest(from: String, to: String, requested: Boolean)

//    @Query("DELETE FROM connections_table")
//    abstract suspend fun deleteAll()
}