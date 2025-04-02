package com.example.iperf3client.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TestUiState::class,ExecutedTestConfig::class,ExecutedTestResults::class], version = 1)
abstract class TestDatabase : RoomDatabase() {


    abstract fun testDao(): TestDao
    abstract fun resultsDao(): TestsWithResultsDAO

    companion object {
        private var instance: TestDatabase? = null
        var needsInitialization: Boolean = true

        @Synchronized
        fun exists():Boolean {
           return instance != null
        }

        @Synchronized
        fun getInstance(ctx: Context): TestDatabase {
            if(instance == null) {
                needsInitialization = true
                instance = Room.databaseBuilder(
                    ctx.applicationContext, TestDatabase::class.java,
                    "iperf3_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }
            needsInitialization = false
            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }



}