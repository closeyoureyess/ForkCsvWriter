package org.writer.myutils.other;

public enum TypeField {

    EMPTY(""),
    LIST("List"),
    SET("Set"),
    MAP("Map"),
    STRING("String"),
    BYTE("Byte"),
    SHORT("Short"),
    INT("Int"),
    LONG("Long"),
    FLOAT("Float"),
    DOUBLE("Double"),
    CHAR("Char"),
    BOOLEAN("Boolean");

    private String typeField;

    TypeField(String enumDescription) {
        this.typeField = enumDescription;
    }

    public String getTypeField() {
        return typeField;
    }

}
