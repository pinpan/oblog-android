package com.applego.oblog.tppwatch.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.applego.oblog.tppwatch.data.model.Role

@Dao
interface RolesDao {
    /**
     * Select all roles from the roles table.
     *
     * @return all roles.
     */
    @Query("SELECT * FROM Roles")
    suspend fun getRoles(): List<Role>

    /**
     * Select a role by id.
     *
     * @param roleId the role id.
     * @return the role with roleId.
     */
    @Query("SELECT * FROM Roles WHERE id = :roleId")
    suspend fun getRoleById(roleId: String): Role?

    /**
     * Insert a Role in the database. If the Role already exists, replace it.
     *
     * @param Role - the Role to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(role: Role)


}
