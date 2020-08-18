package com.applego.oblog.apikey

class ApiKeyHolder (
    var apiKey : String,
    var rateLimit : Long,
    var requestsLimit  : Long
) : AbstractApiKeyHolder {


}