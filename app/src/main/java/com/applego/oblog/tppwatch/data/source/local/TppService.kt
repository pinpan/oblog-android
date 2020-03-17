package com.applego.oblog.tppwatch.data.source.local

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(

    primaryKeys = ["service", "tpp"], tableName = "tpp_services",
    foreignKeys = [
        ForeignKey(
                entity = Psd2Service::class,
                parentColumns = ["id"],
                childColumns = ["service"],
                onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
                entity = Tpp::class,
                parentColumns = ["id"],
                childColumns = ["tpp"],
                onDelete = ForeignKey.CASCADE
        )]
)
class TppService {
    var service: Int = 0
    var tpp: Int = 0
}