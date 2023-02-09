package com.exner.tools.fototimerresearch2.data.persistence

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object FotoTimerDataMigrations {
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE FotoTimerProcess ADD COLUMN keeps_screen_on INTEGER NOT NULL DEFAULT TRUE;")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE FotoTimerProcess ADD COLUMN has_pre_beeps INTEGER NOT NULL DEFAULT FALSE;")
        }
    }

}