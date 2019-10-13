package com.applego.oblog.tppwatch.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TppServicesDao {

    /**
     *  Associate a Service with a Tpp
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
     * Insert a Service in the database. If the Service already exists, replace it.
     *
     * @param Service the Service to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: TppService)

    @Query("SELECT tpps.id as tpp_id, services.title as service_title, tpp_services.service , services.* FROM tpps LEFT OUTER JOIN tpp_services on tpp_services.Tpp = tpps.id LEFT OUTER JOIN services on tpp_services.service = services.id")
    fun getAllTppWithAssignedServices() : List<TppServicePair> //LiveData<>

}
