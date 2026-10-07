package org.example;

public enum RecordType {
    A(1),
    MX(15);

    private final int code;

    RecordType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static RecordType fromCode(int code) {
        for (RecordType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}