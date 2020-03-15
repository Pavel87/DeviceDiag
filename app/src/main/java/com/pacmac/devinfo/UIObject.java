package com.pacmac.devinfo;

public class UIObject {

    private String label;
    private String value;
    private String suffix = null;
    private int type = 0; // 0 - default // 1 - title

    public UIObject(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public UIObject(String label, String value, int type) {
        this.label = label;
        this.value = value;
        this.type = type;
    }

    public UIObject(String label, String value, String suffix) {
        this.label = label;
        this.value = value;
        this.suffix = suffix;
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
