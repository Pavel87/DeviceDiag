package com.pacmac.devinfo.main.model

enum class PermissionState(val status: Int) {
    DENIED(0), // USE AS DEFAULT
    GRANTED(1),  // USE ONLY IF GRANTED
    RATIONAL_DISPLAYED(2),
    DENIED_FOREVER(3), // IF PERM DENIED AFTER RATIONAL DISPLAYED
}