package com.dls.core;

/**
 * Represents a digital logic signal value.
 * Supports the standard binary levels plus UNKNOWN (uninitialized) and
 * HIGH_Z (high impedance / floating), which are common in real HDL simulators.
 */
public enum Signal {
    LOW(0),
    HIGH(1),
    UNKNOWN(-1),
    HIGH_Z(-2);

    private final int value;

    Signal(int value) {
        this.value = value;
    }

    public int toInt() {
        if (this == LOW) return 0;
        if (this == HIGH) return 1;
        throw new IllegalStateException("Cannot convert " + this + " to int");
    }

    public boolean isDefined() {
        return this == LOW || this == HIGH;
    }

    public static Signal fromBoolean(boolean b) {
        return b ? HIGH : LOW;
    }

    public Signal not() {
        if (this == LOW) return HIGH;
        if (this == HIGH) return LOW;
        return UNKNOWN;
    }

    @Override
    public String toString() {
        switch (this) {
            case LOW: return "0";
            case HIGH: return "1";
            case HIGH_Z: return "Z";
            default: return "X";
        }
    }
}
