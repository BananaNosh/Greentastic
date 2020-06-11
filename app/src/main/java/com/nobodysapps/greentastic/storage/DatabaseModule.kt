package com.nobodysapps.greentastic.storage

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabase(context: Context): Database = Room.databaseBuilder(
        context.applicationContext,
        Database::class.java,
        "database"
    ).build()

    @Provides
    fun providesConnectionDao(database: Database): ConnectionDao = database.connectionDao()

}