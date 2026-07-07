package com.dls.gates;

import com.dls.core.Signal;

/** N-input OR gate: output is HIGH if any input is HIGH. */
public class OrGate extends Gate {

    public OrGate(String name, int numInputs) {
        super(name, numInputs);
    }

    public OrGate(String name) {
        this(name, 2);
    }

    @Override
    protected Signal compute(Signal[] inputs) {
        boolean anyUnknown = false;
        for (Signal s : inputs) {
            if (s == Signal.HIGH) return Signal.HIGH;
            if (!s.isDefined()) anyUnknown = true;
        }
        return anyUnknown ? Signal.UNKNOWN : Signal.LOW;
    }
}
