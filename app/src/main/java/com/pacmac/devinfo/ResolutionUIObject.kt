package com.pacmac.devinfo

import com.pacmac.devinfo.camera.model.Resolution

class ResolutionUIObject(val title: String, val resolutions: List<Resolution>): UIObject("", "", ListType.RESOLUTION) {
}