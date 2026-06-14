package com.pacmac.devinfo

open class UIObject(
    val label: String,
    val value: String? = null,
    val type: ListType = ListType.MAIN,
    val state: ThreeState? = null,
    val suffix: String? = null,
) {
    // Mirrors Java constructor: UIObject(label, value, suffix)
    constructor(label: String, value: String?, suffix: String?) :
        this(label = label, value = value, type = ListType.MAIN, state = null, suffix = suffix)

    // Mirrors Java constructor: UIObject(label, state, type)
    constructor(label: String, state: ThreeState?, type: ListType) :
        this(label = label, value = null, type = type, state = state, suffix = null)
}
