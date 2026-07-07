package com.dls.gates;

import com.dls.core.Signal;

/** N-input NAND gate: inverse of AND. */
public class NandGate extends Gate {

    public NandGate(String name, int numInputs) {
        super(name, numInputs);
    }

    public NandGate(String name) {
        this(name, 2);
    }

    @Override
    protected Signal compute(Signal[] inputs) {
        for (Signal s : inputs) {
            if (!s.isDefined()) return Signal.UNKNOWN;
            if (s == Signal.LOW) return Signal.HIGH;
        }
        return Signal.LOW;
    }
}
