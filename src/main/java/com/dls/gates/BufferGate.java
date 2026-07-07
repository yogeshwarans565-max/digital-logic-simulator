package com.dls.gates;

import com.dls.core.Signal;

/** Passes its input straight through; useful for fan-out and delay modeling. */
public class BufferGate extends Gate {

    public BufferGate(String name) {
        super(name, 1);
    }

    @Override
    protected Signal compute(Signal[] inputs) {
        return inputs[0];
    }
}
