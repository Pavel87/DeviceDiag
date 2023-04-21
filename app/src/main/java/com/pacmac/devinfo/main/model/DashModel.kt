package com.pacmac.devinfo.main.model

data class DashModel(
    val dashItem: DashItem,
    val imageRes: Int,
    val actClass: Class<*>,
    val onclick: suspend (DashModel) -> Unit
)
