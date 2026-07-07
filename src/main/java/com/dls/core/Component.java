package com.dls.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base class for anything that can be placed in a circuit: a primitive gate,
 * a flip-flop, or a composite circuit built from other components.
 *
 * Subclasses implement {@link #evaluate()}, which reads current input pin
 * values and computes new output pin values. The Simulator calls evaluate()
 * whenever an input pin changes.
 */
public abstract class Component {

    private final String name;
    private final Map<String, Pin> inputs = new LinkedHashMap<>();
    private final Map<String, Pin> outputs = new LinkedHashMap<>();

    protected Component(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected Pin addInput(String pinName) {
        Pin pin = new Pin(pinName, Pin.Direction.INPUT, this);
        inputs.put(pinName, pin);
        return pin;
    }

    protected Pin addOutput(String pinName) {
        Pin pin = new Pin(pinName, Pin.Direction.OUTPUT, this);
        outputs.put(pinName, pin);
        return pin;
    }

    public Pin getInput(String pinName) {
        Pin pin = inputs.get(pinName);
        if (pin == null) throw new IllegalArgumentException("No such input pin: " + pinName + " on " + name);
        return pin;
    }

    public Pin getOutput(String pinName) {
        Pin pin = outputs.get(pinName);
        if (pin == null) throw new IllegalArgumentException("No such output pin: " + pinName + " on " + name);
        return pin;
    }

    public Map<String, Pin> getInputs() {
        return inputs;
    }

    public Map<String, Pin> getOutputs() {
        return outputs;
    }

    /**
     * Recomputes this component's outputs from its current input values.
     * Must be pure with respect to input pin values (no hidden state changes
     * outside sequential elements, which manage their own state explicitly).
     */
    public abstract void evaluate();

    @Override
    public String toString() {
        return name;
    }
}
