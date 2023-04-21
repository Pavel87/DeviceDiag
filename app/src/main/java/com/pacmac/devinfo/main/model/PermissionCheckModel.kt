package com.pacmac.devinfo.main.model

data class PermissionCheckModel(val permissionMsg: Int, val permissionDisabledMsg: Int, val permissions: Array<String>, val permissionState: PermissionState)
