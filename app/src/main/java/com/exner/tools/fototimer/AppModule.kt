package com.exner.tools.fototimer

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.exner.tools.fototimer.data.persistence.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

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

    @Provides
    fun provideDefaultSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }
}