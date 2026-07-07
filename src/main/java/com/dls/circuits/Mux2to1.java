package com.dls.circuits;

import com.dls.core.Pin;
import com.dls.core.Signal;

/**
 * 2-to-1 multiplexer: Y = SEL ? B : A
 * Implemented directly rather than from gates for clarity/performance,
 * but follows the same Component contract as everything else.
 */
public class Mux2to1 extends CompositeCircuit {

    private final Pin a, b, sel, y;

    public Mux2to1(String name) {
        super(name);
        a = addInput("A");
        b = addInput("B");
        sel = addInput("SEL");
        y = addOutput("Y");
    }

    @Override
    public void evaluate() {
        Signal s = sel.getValue();
        if (s == Signal.HIGH) {
            y.setValue(b.getValue());
        } else if (s == Signal.LOW) {
            y.setValue(a.getValue());
        } else {
            y.setValue(Signal.UNKNOWN);
        }
    }

    public Pin a() { return a; }
    public Pin b() { return b; }
    public Pin sel() { return sel; }
    public Pin y() { return y; }
}
