package com.subnix.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Subscription::class], version = 1, exportSchema = false)
abstract class SubnixDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: SubnixDatabase? = null

        fun getDatabase(context: Context): SubnixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SubnixDatabase::class.java,
                    "subnix_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
