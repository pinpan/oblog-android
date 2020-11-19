package com.applego.oblog.tppwatch.data.model

class Contact constructor (t: ContactType, v: String) {
    var type: ContactType ?= t
    var value: String = v
}