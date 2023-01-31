package com.pacmac.devinfo;

import androidx.annotation.NonNull;

public class UIObject {

    private String label;
    private String value;
    private String suffix = null;
    @NonNull
    private ListType type = ListType.MAIN;
    private ThreeState state;

    public UIObject(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public UIObject(String label, String value, @NonNull ListType type) {
        this.label = label;
        this.value = value;
        this.type = type;
    }

    public UIObject(String label, ThreeState state, @NonNull ListType type) {
        this.label = label;
        this.state = state;
        this.type = type;
    }

    public UIObject(String label, String value, String suffix) {
        this.label = label;
        this.value = value;
        this.suffix = suffix;
    }

    public ThreeState getState() {
        return state;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getSuffix() {
        return suffix;
    }

    @NonNull
    public ListType getType() {
        return type;
    }
}
