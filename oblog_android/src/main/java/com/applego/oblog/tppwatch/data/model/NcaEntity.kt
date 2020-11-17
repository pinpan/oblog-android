package com.applego.oblog.tppwatch.data.model

import androidx.room.*
import com.applego.oblog.tppwatch.data.convertor.OblogTypeConverters
import java.util.*

/**
 * Immutable model class for a Tpp. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param entityId    TPP legal entity ID provided by home country
 * @param entityCode  entityCode assigned by EBA. Inconsistently includes TPP home country, Tpp Home NCA ID and TPP's NCA given ID
 * @param entityName  entityName of the tpp
 * @param description description of the tpp
 * @param isFollowed  whether or not this tpp is followed
 * @param id          id of the tpp
 */
@Entity(tableName = "nca_entity")
@TypeConverters(OblogTypeConverters::class)
data class NcaEntity @JvmOverloads constructor(
        @ColumnInfo(name = "entityId") val _entityId: String = "",
        @ColumnInfo(name = "entityCode") val _entityCode: String = "",
        @ColumnInfo(name = "entityName") var _entityName: String = "",
        @ColumnInfo(name = "description") var _description: String = "",
        @ColumnInfo(name = "globalUrn") var _globalUrn: String = "",
        @ColumnInfo(name = "ebaEntityVersion") var _ebaEntityVersion: String = "",
        @ColumnInfo(name = "country") var _country: String = "",

        @PrimaryKey @ColumnInfo(name = "id") var _id: String = UUID.randomUUID().toString()
)  : OblogEntity() {

    @Ignore
    fun getEntityId() = _entityId

    @Ignore
    fun getEntityCode() = _entityCode

    @Ignore
    fun getEntityName() = _entityName

    @Ignore
    fun getDescription() = _description

    @Ignore
    fun getGlobalUrn() = _globalUrn

    @Ignore
    fun getEbaEntityVersion() = _ebaEntityVersion

    @Ignore
    fun getCountry() = _country

    @Ignore
    fun getId() = _id

    fun getTitleForList(): String {
        return getEntityName()
    }


    // TODO#: Consider Following fields
    // Address -> G Maps

    //  Details aka properties from CNB
    //
        // TODO: "nazevRole": "Banka"
        // TODO: "kodRole": 1,

        // "ncaProperties {
            //"icoTyp": "ICO",
            //"typ": "P",
            //"ico": "25672720",
        // }
    // telefon: 224441111 Fax	                 	E-mail	podatelna@moneta.cz	www	www.moneta.cz
    // Číselný kód (Bank code)	0600
    // ??? LEI	I6USJ58BDV2BO5KP3C31

    // Povolené činnosti
    // Související vazby
    // Historie subjektu

    // Přeshraniční služby
}


/*
 curl -X GET https://api.oblog.org/api/nca-registry/CZ?name=Money -H "accept: * / * " -H "X-Api-Key: T11NOL41x0L7Cn4OAc1FNQogHAcpWvQA"
{
    "content": [
        {
            "entityId": "25672720",
            "entityName": [
                "MONETA Money Bank, a.s."
            ],
            "entityType": "PSD_NCA_CZ",
            "entityCode": "25672720",
            "services": null,
            "ebaProperties": null,
            "ncaProperties": {
                "nazev": "MONETA Money Bank, a.s.",
                "icoTyp": "ICO",
                "typ": "P",
                "datumVzniku": "1998-06-09",
                "ico": "25672720",
                "adresa": {
                    "ulice": "Vyskočilova",
                    "cisloPopisne": 1442,
                    "cisloOrientacni": 1,
                    "cisloOrientacniPismeno": "b",
                    "castObce": "Michle",
                    "obec": "Praha",
                    "psc": "14000",
                    "ruian": 25559869,
                    "nestrukturovanaAdresa": "Vyskočilova 1442/1b, Michle, 140 00 Praha 4",
                    "stat": "CZ"
                },
                "kodRole": 1,
                "nazevRole": "Banka"
            },
            "entityVersion": null
        },
        {
            "entityId": "25672720",
            "entityName": [
                "MONETA Money Bank, a.s."
            ],
            "entityType": "PSD_NCA_CZ",
            "entityCode": "25672720",
            "services": null,
            "ebaProperties": null,
            "ncaProperties": {
                "nazev": "MONETA Money Bank, a.s.",
                "icoTyp": "ICO",
                "typ": "P",
                "datumVzniku": "1998-06-09",
                "ico": "25672720",
                "adresa": {
                    "ulice": "Vyskočilova",
                    "cisloPopisne": 1442,
                    "cisloOrientacni": 1,
                    "cisloOrientacniPismeno": "b",
                    "castObce": "Michle",
                    "obec": "Praha",
                    "psc": "14000",
                    "ruian": 25559869,
                    "nestrukturovanaAdresa": "Vyskočilova 1442/1b, Michle, 140 00 Praha 4",
                    "stat": "CZ"
                },
                "kodRole": 1,
                "nazevRole": "Banka"
            },
            "entityVersion": null
        },
        {
            "entityId": "06518648",
            "entityName": [
                "Global Money Shift s.r.o."
            ],
            "entityType": "PSD_NCA_CZ",
            "entityCode": "06518648",
            "services": null,
            "ebaProperties": null,
            "ncaProperties": {
                "nazev": "Global Money Shift s.r.o.",
                "icoTyp": "ICO",
                "typ": "P",
                "datumVzniku": "2018-01-13",
                "ico": "06518648",
                "adresa": {
                    "ulice": "Pernerova",
                    "cisloPopisne": 676,
                    "cisloOrientacni": 51,
                    "cisloOrientacniPismeno": null,
                    "castObce": "Karlín",
                    "obec": "Praha",
                    "psc": "18600",
                    "ruian": 72663006,
                    "nestrukturovanaAdresa": "Pernerova 676/51, Karlín, 186 00 Praha 8",
                    "stat": "CZ"
                },
                "kodRole": 77,
                "nazevRole": "Poskytovatel platebních služeb malého rozsahu"
            },
            "entityVersion": null
        },
        {
            "entityId": "25672720",
            "entityName": [
                "MONETA Money Bank, a.s."
            ],
            "entityType": "PSD_NCA_CZ",
            "entityCode": "25672720",
            "services": null,
            "ebaProperties": null,
            "ncaProperties": {
                "nazev": "MONETA Money Bank, a.s.",
                "icoTyp": "ICO",
                "typ": "P",
                "datumVzniku": "1998-06-09",
                "ico": "25672720",
                "adresa": {
                    "ulice": "Vyskočilova",
                    "cisloPopisne": 1442,
                    "cisloOrientacni": 1,
                    "cisloOrientacniPismeno": "b",
                    "castObce": "Michle",
                    "obec": "Praha",
                    "psc": "14000",
                    "ruian": 25559869,
                    "nestrukturovanaAdresa": "Vyskočilova 1442/1b, Michle, 140 00 Praha 4",
                    "stat": "CZ"
                },
                "kodRole": 1,
                "nazevRole": "Banka"
            },
            "entityVersion": null
        },
        {
            "entityId": "24803316",
            "entityName": [
                "MoneyPolo Europe s.r.o."
            ],
            "entityType": "PSD_NCA_CZ",
            "entityCode": "24803316",
            "services": null,
            "ebaProperties": null,
            "ncaProperties": {
                "nazev": "MoneyPolo Europe s.r.o.",
                "icoTyp": "ICO",
                "typ": "P",
                "datumVzniku": "2018-01-13",
                "ico": "24803316",
                "adresa": {
                    "ulice": "Archeologická",
                    "cisloPopisne": 2256,
                    "cisloOrientacni": 1,
                    "cisloOrientacniPismeno": null,
                    "castObce": "Stodůlky",
                    "obec": "Praha",
                    "psc": "15500",
                    "ruian": 22124772,
                    "nestrukturovanaAdresa": "Archeologická 2256/1, Stodůlky, 155 00 Praha 5",
                    "stat": "CZ"
                },
                "kodRole": 77,
                "nazevRole": "Poskytovatel platebních služeb malého rozsahu"
            },
            "entityVersion": null
        },
        {
            "entityId": "27380050",
            "entityName": [
                "Money Store s.r.o."
            ],
            "entityType": "PSD_NCA_CZ",
            "entityCode": "27380050",
            "services": null,
            "ebaProperties": null,
            "ncaProperties": {
                "nazev": "Money Store s.r.o.",
                "icoTyp": "ICO",
                "typ": "P",
                "datumVzniku": "2018-01-13",
                "ico": "27380050",
                "adresa": {
                    "ulice": "Korunní",
                    "cisloPopisne": 810,
                    "cisloOrientacni": 104,
                    "cisloOrientacniPismeno": null,
                    "castObce": "Vinohrady",
                    "obec": "Praha",
                    "psc": "10100",
                    "ruian": 22650334,
                    "nestrukturovanaAdresa": "Korunní 810/104, Vinohrady, 101 00 Praha 10",
                    "stat": "CZ"
                },
                "kodRole": 77,
                "nazevRole": "Poskytovatel platebních služeb malého rozsahu"
            },
            "entityVersion": null
        },
        {
            "entityId": "27378527",
            "entityName": [
                "Money Change s.r.o."
            ],
            "entityType": "PSD_NCA_CZ",
            "entityCode": "27378527",
            "services": null,
            "ebaProperties": null,
            "ncaProperties": {
                "nazev": "Money Change s.r.o.",
                "icoTyp": "ICO",
                "typ": "P",
                "datumVzniku": "2018-01-13",
                "ico": "27378527",
                "adresa": {
                    "ulice": "Na hutích",
                    "cisloPopisne": 581,
                    "cisloOrientacni": 1,
                    "cisloOrientacniPismeno": null,
                    "castObce": "Dejvice",
                    "obec": "Praha",
                    "psc": "16000",
                    "ruian": 22190732,
                    "nestrukturovanaAdresa": "Na hutích 581/1, Dejvice, 160 00 Praha 6",
                    "stat": "CZ"
                },
                "kodRole": 77,
                "nazevRole": "Poskytovatel platebních služeb malého rozsahu"
            },
            "entityVersion": null
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "pageNumber": 0,
        "pageSize": 10,
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 7,
    "first": true,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "number": 0,
    "numberOfElements": 7,
    "size": 10,
    "empty": false
}
 */
