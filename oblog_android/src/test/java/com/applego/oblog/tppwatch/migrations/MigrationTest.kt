package com.applego.oblog.tppwatch.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4 //ext.junit.
import androidx.test.platform.app.InstrumentationRegistry
import com.applego.oblog.tppwatch.data.source.local.TppDatabase
import com.applego.oblog.tppwatch.util.ServiceLocator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    companion object {
        private const val TEST_DB = "migration-test"

        @get:Rule /* @JvmField*/
        val helper: MigrationTestHelper = MigrationTestHelper(
                InstrumentationRegistry.getInstrumentation(),
                TppDatabase::class.java.canonicalName,
                FrameworkSQLiteOpenHelperFactory())
    }

    @Test
    fun signInTest() {
    }

    //@Test
    fun migrationFrom44To45() {
        //Create database with version 45
        val db = helper.createDatabase(TEST_DB, 45)
        val values = ContentValues().apply {
            put("id", 5)
        }
        db.insert("User", SQLiteDatabase.CONFLICT_REPLACE, values)

        helper.runMigrationsAndValidate(TEST_DB, 1, true, ServiceLocator.MIGRATION_44_45)
    }
}