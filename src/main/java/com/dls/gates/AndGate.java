package com.dls.gates;

import com.dls.core.Signal;

/** N-input AND gate: output is HIGH only if every input is HIGH. */
public class AndGate extends Gate {

    public AndGate(String name, int numInputs) {
        super(name, numInputs);
    }

    public AndGate(String name) {
        this(name, 2);
    }

    @Override
    protected Signal compute(Signal[] inputs) {
        for (Signal s : inputs) {
            if (!s.isDefined()) return Signal.UNKNOWN;
            if (s == Signal.LOW) return Signal.LOW;
        }
        return Signal.HIGH;
    }
}
