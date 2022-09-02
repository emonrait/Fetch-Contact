package com.emon.raihan.contactfeatch

data class Contact(

    var id: Int = 0,
    var name: String?,
    var number: String?,
    var uri: String?,

) {
    constructor(name: String?, number: String?, uri: String?) : this(0, name, number, uri)

}