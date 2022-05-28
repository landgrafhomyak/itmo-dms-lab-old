package com.github.landgrafhomyak.itmo.dms_lab.requests

interface RequestMeta {
    val description: String
    @Suppress("unused")
    val consoleName: String?
        get() = null
}