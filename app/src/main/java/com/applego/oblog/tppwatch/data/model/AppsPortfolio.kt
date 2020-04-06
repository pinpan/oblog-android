package com.applego.oblog.tppwatch.data.model

import androidx.room.*
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import java.util.*

@Entity(tableName = "apps")
@TypeConverters(OblogTypeConverters::class)
data class AppsPortfolio @JvmOverloads constructor(
        @ColumnInfo(name = "tppId") var tppId: String = "",
        @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {

    /**
     * Map of TPP's apps
     */
    @Embedded
    var appsMap = HashMap<String, List<App>>()

    @Embedded
    var appsList: List<App> = ArrayList<App>()

}
