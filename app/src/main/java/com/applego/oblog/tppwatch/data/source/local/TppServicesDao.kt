package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TppServicesDao {

    /**
     *  Associate a Psd2Service with a Tpp
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTppService( tppService: TppService)


    /**
     *  Remove all services for given Tpp
     */
    @Query("DELETE FROM tpp_services WHERE tpp_services.tpp =:tppId")
    fun removeAllAssignedServiceByTppId( tppId : Int)


    /**
     * Remove specific service for given Tpp
     */
    @Query("DELETE FROM tpp_services WHERE tpp_services.tpp =:tppId and tpp_services.service =:serviceId")
    fun removeTppServiceForTpp( tppId: Int, serviceId : Int)


    /**
     * Select all services for given Tpp
     *
     * @return all services.
     */
    @Query("SELECT * FROM tpp_services WHERE tpp =:tppId")
    suspend fun getTppServices(tppId: Int): List<TppService>


    /**
     * Select a service by id for All Tpps.
     *
     * @param serviceId the service id.
     * @return the service with serviceId.
     */
    @Query("SELECT * FROM tpp_services WHERE service = :serviceId")
    suspend fun getServiceById(serviceId: String): TppService?

    /**
     * Insert a Psd2Service in the database. If the Psd2Service already exists, replace it.
     *
     * @param Psd2Service the Psd2Service to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: TppService)

}
