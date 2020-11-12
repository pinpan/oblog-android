package com.applego.oblog.tppwatch.data.dao

import androidx.room.*
import com.applego.oblog.tppwatch.data.model.App

/**
 * Data Access Object for the apps table.
 */
@Dao
interface AppEntityDao {

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
    @Query("SELECT * FROM Apps WHERE id = :appId")
    suspend fun getAppById(appId: String): App?

    /**
     * Select a app by id.
     *
     * @param appId the app id.
     * @return the App with appId.
     */
    @Query("SELECT * FROM Apps WHERE name like :appName and tppID=:tppId")
    suspend fun getAppByName(appName: String, tppId: String): App?

    /**
     * Insert a App in the database. If the App already exists, replace it.
     *
     * @param App - the App to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: App)

    /**
     * Update an app
     *
     * @param app App to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateApp(аpp: App) : Int

    /**
     * Update an app
     *
     * @param app App to be updated
     */
    @Delete
    suspend fun deleteApp(аpp: App)
}
