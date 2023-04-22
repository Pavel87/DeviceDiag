package com.pacmac.devinfo.main.model

enum class PermissionState(val status: Int) {
    DENIED(0), // USE AS DEFAULT
    GRANTED(10),  // USE ONLY IF GRANTED
    RATIONAL_DISPLAYED(20),
    DENIED_FOREVER(30), // IF PERM DENIED AFTER RATIONAL DISPLAYED
}