package com.exner.tools.fototimerresearch2

import android.content.Context
import com.exner.tools.fototimerresearch2.data.persistence.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFotoTimerDAO(@ApplicationContext appContext: Context): FotoTimerProcessDAO {
        return FotoTimerProcessRoomDatabase.getDatabase(appContext, CoroutineScope(SupervisorJob()))
            .processDAO()
    }

    @Provides
    fun provideFotoTimerProcessRepository(processDAO: FotoTimerProcessDAO) =
        FotoTimerProcessRepository(processDAO)
}