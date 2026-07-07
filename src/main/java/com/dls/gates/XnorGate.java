package com.dls.gates;

import com.dls.core.Signal;

/** N-input XNOR gate: inverse of XOR (HIGH when an even number of inputs are HIGH). */
public class XnorGate extends Gate {

    public XnorGate(String name, int numInputs) {
        super(name, numInputs);
    }

    public XnorGate(String name) {
        this(name, 2);
    }

    @Override
    protected Signal compute(Signal[] inputs) {
        int highCount = 0;
        for (Signal s : inputs) {
            if (!s.isDefined()) return Signal.UNKNOWN;
            if (s == Signal.HIGH) highCount++;
        }
        return Signal.fromBoolean(highCount % 2 == 0);
    }
}
