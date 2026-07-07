package com.dls.core;

/**
 * A Wire connects one driving output Pin to one receiving input Pin.
 * Propagation delay (in simulation time units) can be modeled per-wire,
 * which is what allows the waveform viewer to show realistic timing.
 */
public class Wire {

    private final Pin source;
    private final Pin target;
    private final int delay; // simulation ticks before target sees the new value
    private String label;

    public Wire(Pin source, Pin target) {
        this(source, target, 0);
    }

    public Wire(Pin source, Pin target, int delay) {
        if (source.getDirection() != Pin.Direction.OUTPUT) {
            throw new IllegalArgumentException("Wire source must be an OUTPUT pin: " + source);
        }
        if (target.getDirection() != Pin.Direction.INPUT) {
            throw new IllegalArgumentException("Wire target must be an INPUT pin: " + target);
        }
        this.source = source;
        this.target = target;
        this.delay = delay;
        source.addWire(this);
        target.addWire(this);
    }

    public Pin getSource() {
        return source;
    }

    public Pin getTarget() {
        return target;
    }

    public int getDelay() {
        return delay;
    }

    public String getLabel() {
        return label;
    }

    public Wire setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return source + " --(" + delay + ")--> " + target;
    }
}
