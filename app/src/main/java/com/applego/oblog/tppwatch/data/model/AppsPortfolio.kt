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
    //@Embedded
    //private var appsMap = HashMap<String, List<App>>()

    @Embedded
    var appsList/*: List<App>*/ = ArrayList<App>()

    public fun addApp(app: App) {
        //appsMap.put(app.name, app);
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
