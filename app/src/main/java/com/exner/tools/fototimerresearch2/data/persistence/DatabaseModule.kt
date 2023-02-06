package com.exner.tools.fototimerresearch2.data.persistence

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideLogDao(database: FotoTimerProcessRoomDatabase): FotoTimerProcessDAO {
        return database.processDAO()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): FotoTimerProcessRoomDatabase {
        return Room.databaseBuilder(
            appContext,
            FotoTimerProcessRoomDatabase::class.java,
            "logging.db"
        ).build()
    }
}