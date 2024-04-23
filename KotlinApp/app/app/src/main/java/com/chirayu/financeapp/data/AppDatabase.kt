package com.chirayu.financeapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chirayu.financeapp.R
import com.chirayu.financeapp.data.dao.MovementDao
import com.chirayu.financeapp.data.dao.SubscriptionDao
import com.chirayu.financeapp.data.dao.TagDao
import com.chirayu.financeapp.model.entities.Budget
import com.chirayu.financeapp.model.entities.Movement
import com.chirayu.financeapp.model.entities.Subscription
import com.chirayu.financeapp.model.entities.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Database(entities = [Movement::class, Subscription::class, Tag::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movementDao(): MovementDao

    abstract fun subscriptionDao(): SubscriptionDao

    abstract fun tagDao(): TagDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope,
        private val cxt: Context
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(scope.coroutineContext, CoroutineStart.UNDISPATCHED) {
                    populateDb(database.tagDao())
                }
            }
        }

        private suspend fun populateDb(dao: TagDao) {
            if (dao.getAll().first().isNotEmpty()) {
                return
            }

            dao.deleteAll()

            dao.insert(
                Tag(0, cxt.getString(R.string.bets), R.color.purple_200, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.clothes), R.color.emerald_200, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.culture), R.color.red_200, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.entertainment), R.color.green_200, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.food), R.color.cyan_200, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.gifts), R.color.blue_200, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.holidays), R.color.purple_800, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.personal_care), R.color.emerald_800, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.others), R.color.red_800, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.sport), R.color.green_800, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.tech), R.color.cyan_800, false)
            )
            dao.insert(
                Tag(0, cxt.getString(R.string.transports), R.color.blue_800, false)
            )
            dao.insert(
                Tag(0,"Stocks",R.color.green_200,true)
            )
            dao.insert(
                Tag(0,"Wages",R.color.black,true)
            )
            dao.insert(
                Tag(0,"Salary",R.color.cyan_200,true)
            )
            dao.insert(
                Tag(0,"Bonuses",R.color.emerald_400,true)
            )
            dao.insert(
                Tag(0,"Tips",R.color.emerald_200,true)
            )
            dao.insert(
                Tag(0,"Commission",R.color.red_200,true)
            )
            dao.insert(
                Tag(0,"Gifts",R.color.red_50,true)
            )
            dao.insert(
                Tag(0,"Royalty",R.color.blue_200,true)
            )
            dao.insert(
                Tag(0,"Pension",R.color.green_200,true)
            )
            dao.insert(
                Tag(0,"Other",R.color.blue_300,true)
            )
        }
    }

    companion object {
        private val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tags ADD COLUMN isIncome INTEGER DEFAULT 0 NOT NULL")
                db.execSQL("UPDATE tags SET isIncome = 1 WHERE id = 1")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "saveapp_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(AppDatabaseCallback(scope, context))
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
