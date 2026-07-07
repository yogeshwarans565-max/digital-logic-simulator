package com.dls.core;

import java.util.ArrayList;
import java.util.List;

/**
 * A Pin is a named connection point on a Component (an input or an output).
 * Output pins can drive one or more Wires; input pins receive a value from
 * exactly one driving wire.
 */
public class Pin {

    public enum Direction { INPUT, OUTPUT }

    private final String name;
    private final Direction direction;
    private final Component owner;
    private Signal value = Signal.UNKNOWN;
    private final List<Wire> connectedWires = new ArrayList<>();

    public Pin(String name, Direction direction, Component owner) {
        this.name = name;
        this.direction = direction;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public Direction getDirection() {
        return direction;
    }

    public Component getOwner() {
        return owner;
    }

    public Signal getValue() {
        return value;
    }

    /**
     * Sets the value on this pin. Returns true if the value actually changed,
     * which the simulator uses to decide whether to propagate further.
     */
    public boolean setValue(Signal newValue) {
        boolean changed = this.value != newValue;
        this.value = newValue;
        return changed;
    }

    void addWire(Wire wire) {
        connectedWires.add(wire);
    }

    public List<Wire> getConnectedWires() {
        return connectedWires;
    }

    @Override
    public String toString() {
        return owner.getName() + "." + name + "=" + value;
    }
}
