package com.dls.gates;

import com.dls.core.Signal;

/** Single-input inverter. */
public class NotGate extends Gate {

    public NotGate(String name) {
        super(name, 1);
    }

    @Override
    protected Signal compute(Signal[] inputs) {
        return inputs[0].not();
    }
}
