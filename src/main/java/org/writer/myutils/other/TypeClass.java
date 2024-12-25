package org.writer.myutils.other;

public enum TypeClass {

    EMPTY(""),
    WR("Wrapper"),
    PR("Primitive");

    private String typeClass;

    TypeClass(String enumDescription) {
        this.typeClass = enumDescription;
    }

    public String getTypeClass() {
        return typeClass;
    }
}
