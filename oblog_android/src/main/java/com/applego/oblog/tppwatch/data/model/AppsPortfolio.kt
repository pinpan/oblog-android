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

    @Embedded
    var appsList = ArrayList<App>()

    public fun addApp(app: App) {
        appsList.add(app);
    }

    fun getApp(appId: String) : App? {
        appsList.forEach({
          if (it.id.equals(appId)) {
              return it
          }
        })
        return null
    }
}
