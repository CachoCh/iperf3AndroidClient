package com.example.iperf3client.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TestUiState::class,ExecutedTestConfig::class,ExecutedTestResults::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ])
abstract class TestDatabase : RoomDatabase() {


    abstract fun testDao(): TestDao
    abstract fun resultsDao(): TestsWithResultsDAO

    companion object {
        private var instance: TestDatabase? = null
        const val DATABASE_NAME = "iperf3_database"

        @Synchronized
        fun getInstance(ctx: Context): TestDatabase {
            if(instance == null) {
                instance = Room.databaseBuilder(
                    ctx.applicationContext, TestDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }

            return instance!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }



}