package com.pacmac.devinfo;

public class UIObject {

    private String label;
    private String value;
    private String suffix = null;
    private int type = 0; // 0 - default // 1 - title // 2 icon
    private ThreeState state;

    public UIObject(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public UIObject(String label, String value, int type) {
        this.label = label;
        this.value = value;
        this.type = type;
    }

    public UIObject(String label, ThreeState state, int type) {
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

    public int getType() {
        return type;
    }
}
