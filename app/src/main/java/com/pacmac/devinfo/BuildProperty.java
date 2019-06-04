package com.pacmac.devinfo;

public class BuildProperty {

    final String key;
    final String value;

    public BuildProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
