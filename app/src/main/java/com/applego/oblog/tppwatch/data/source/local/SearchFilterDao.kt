package com.applego.oblog.tppwatch.data.source.local

import androidx.room.*

@Dao
interface SearchFilterDao {
    /**
     * Select all SearchFilters from the DB.
     *
     * @return all SearchFilters.
     */
    @Query("SELECT * FROM SearchFilter")
    suspend fun getSearchFilter(): SearchFilter

    /**
     * Select a role by id.
     *
     * @param roleId the role id.
     * @return the role with roleId.
     */
    @Query("SELECT * FROM Roles WHERE id = :timenow")
    suspend fun getLastSearchFilter(timenow: Long): SearchFilter?

    /**
     * Insert a SearchFilter in the database.
     *
     * @param SearchFilter - the SearchFilter to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(searchFilter: SearchFilter)

    /**
     * Delete SearchFilter from the database.
     *
     * @param SearchFilter - the SearchFilter to be deleted.
     */
    @Delete
    suspend fun deleteSearchFilter(searchFilter: SearchFilter)
}
