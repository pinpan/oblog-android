package com.applego.oblog.tppwatch.data.model

import androidx.room.*
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import com.applego.oblog.tppwatch.data.source.local.RecordStatus
import java.util.*

/**
 * Immutable model class for a Tpp. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param id          id of the tpp
 */
@Entity(tableName = "tpps")
@TypeConverters(OblogTypeConverters::class)
open class OblogEntity() { //@JvmOverloads constructor() {

/*
    @PrimaryKey
    @ColumnInfo(name = "db_id")
    var _db_id: String = UUID.randomUUID().toString()
*/

    @ColumnInfo(name = "fis")
    var fis: Boolean = false

    @ColumnInfo(name = "psd2")
    var psd2: Boolean = false

    @ColumnInfo(name = "followed")
    var followed: Boolean = false

    @ColumnInfo(name = "used")
    var used: Boolean = false

    @ColumnInfo(name = "status")
    var _status: RecordStatus = RecordStatus.NEW


/*
    @Ignore
    fun isFollowed() : Boolean = followed

    @Ignore
    fun isUsed() : Boolean = used

    @Ignore
    fun isFis(): Boolean = fis

    @Ignore
    override fun isPsd2(): Boolean = psd2

    @Ignore
    override fun getStatus() = _status

    override fun getTitleForList(): String {
        return (getEntityName()) ?: getDescription()
    }

*/
    // TODO#: Consider Following fields
    //  details aka properties from EBA
    //  tppRoles, - CZ has, Eba hasn't
    //  apps,
}
