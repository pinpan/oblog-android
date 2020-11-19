package com.applego.oblog.tppwatch.data.model

open abstract class NcaEntityProperties constructor(country: String, bcaName: String) {

    val ncaProperties = HashMap<String, Any>()

    fun getPropertyNames() : Set<String> {
        return ncaProperties.keys
    }

    fun getProperty(key: String) : Any? {
        return ncaProperties.get(key)
    }

    fun initialize(initFrom : Map<String, Any>) {
        ncaProperties.clear()
        ncaProperties.putAll(initFrom)
    }

    fun addProperty(key: String, value: Any) {
        ncaProperties.put(key, value)
    }
}
