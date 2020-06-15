package com.nobodysapps.greentastic.storage

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nobodysapps.greentastic.ui.transport.Vehicle

@Dao
abstract class ConnectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(connection: Connection)

    @Query("SELECT vehicles FROM connections_table WHERE requested=1")
    protected abstract suspend fun load(): Converters.VehiclesWrapper?

    suspend fun loadRequestedVehicles(): List<Vehicle> {
        return load()?.vehicles ?: emptyList()
    }

    @Query("UPDATE connections_table SET requested=0")
    abstract suspend fun resetRequested()

//    @Update(entity = Connection::class)
//    abstract suspend fun unrequest(from: String, to: String, requested: Boolean)

//    @Query("DELETE FROM connections_table")
//    abstract suspend fun deleteAll()
}