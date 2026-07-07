package com.dls.gates;

import com.dls.core.Signal;

/** N-input XOR gate: output is HIGH if an odd number of inputs are HIGH. */
public class XorGate extends Gate {

    public XorGate(String name, int numInputs) {
        super(name, numInputs);
    }

    public XorGate(String name) {
        this(name, 2);
    }

    @Override
    protected Signal compute(Signal[] inputs) {
        int highCount = 0;
        for (Signal s : inputs) {
            if (!s.isDefined()) return Signal.UNKNOWN;
            if (s == Signal.HIGH) highCount++;
        }
        return Signal.fromBoolean(highCount % 2 == 1);
    }
}
