package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppDao {
    /**
     * Select all apps from the apps table.
     *
     * @return all apps.
     */
    @Query("SELECT * FROM Apps")
    suspend fun getApps(): List<App>

    /**
     * Select a app by id.
     *
     * @param appId the app id.
     * @return the App with appId.
     */
    @Query("SELECT * FROM Apps WHERE appid = :appId")
    suspend fun getAppById(appId: String): App?

    /**
     * Insert a App in the database. If the App already exists, replace it.
     *
     * @param App - the App to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: App)

}
