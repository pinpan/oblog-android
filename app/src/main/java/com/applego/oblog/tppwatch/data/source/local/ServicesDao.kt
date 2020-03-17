package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ServicesDao {
    /**
     * Select all services from the services table.
     *
     * @return all services.
     */
    @Query("SELECT * FROM Services")
    suspend fun getServices(): List<Psd2Service>

    /**
     * Select a service by id.
     *
     * @param serviceId the service id.
     * @return the service with serviceId.
     */
    @Query("SELECT * FROM Services WHERE id = :serviceId")
    suspend fun getServiceById(serviceId: String): Psd2Service?

    /**
     * Insert a Psd2Service in the database. If the Psd2Service already exists, replace it.
     *
     * @param Psd2Service the Psd2Service to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Psd2Service)


}
