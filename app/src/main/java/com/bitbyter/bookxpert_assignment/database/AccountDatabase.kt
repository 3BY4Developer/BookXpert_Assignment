package com.bitbyter.bookxpert_assignment.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AccountEntity::class], version = 1)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AccountDatabase? = null

        fun getInstance(context: Context): AccountDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AccountDatabase::class.java,
                    "account_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

