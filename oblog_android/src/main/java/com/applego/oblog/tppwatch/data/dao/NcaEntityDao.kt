package com.applego.oblog.tppwatch.data.dao

import androidx.room.*
import com.applego.oblog.tppwatch.data.model.NcaEntity

/**
 * Data Access Object for the tpps table.
 */
@Dao
interface NcaEntityDao {

    /**
     * Select a tpp by EBA entityId (provided by OBLOG backend).
     *
     * @param encaEntityId the NCA tpp entityId.
     * @return the tpp with entityId.
     */
    @Query("SELECT * FROM nca_entity WHERE entityId = :entityId")
    suspend fun getNcaEntityById(entityId: String): NcaEntity?

    /**
     * Insert or Update an NcaEntity in the database. If the NCaEntity already exists, replace it.
     *
     * @param ncaEntity the NcaEntity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNcaEntity(ncaEntity: NcaEntity)

    /**
     * Update an EbaEntity.
     *
     * @param ncaEntity an NcaEntity to be updated
     * @return the number of NcaEntity-s updated. This should always be 1.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNcaEntity(ncaEntity: NcaEntity): Int
}
