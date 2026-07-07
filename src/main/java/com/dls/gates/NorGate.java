package com.dls.gates;

import com.dls.core.Signal;

/** N-input NOR gate: inverse of OR. */
public class NorGate extends Gate {

    public NorGate(String name, int numInputs) {
        super(name, numInputs);
    }

    public NorGate(String name) {
        this(name, 2);
    }

    @Override
    protected Signal compute(Signal[] inputs) {
        boolean anyUnknown = false;
        for (Signal s : inputs) {
            if (s == Signal.HIGH) return Signal.LOW;
            if (!s.isDefined()) anyUnknown = true;
        }
        return anyUnknown ? Signal.UNKNOWN : Signal.HIGH;
    }
}
