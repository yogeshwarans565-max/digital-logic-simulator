package com.dls.circuits;

import com.dls.core.Pin;
import com.dls.gates.AndGate;
import com.dls.gates.XorGate;

/**
 * Half adder: adds two single bits.
 * SUM = A XOR B
 * CARRY = A AND B
 */
public class HalfAdder extends CompositeCircuit {

    private final Pin a, b, sum, carry;

    public HalfAdder(String name) {
        super(name);
        XorGate xor = add(new XorGate(name + ".xor"));
        AndGate and = add(new AndGate(name + ".and"));

        a = exposeInput("A", xor.in(0), and.in(0));
        b = exposeInput("B", xor.in(1), and.in(1));

        sum = exposeOutput("SUM", xor.out());
        carry = exposeOutput("CARRY", and.out());
    }

    public Pin a() { return a; }
    public Pin b() { return b; }
    public Pin sum() { return sum; }
    public Pin carry() { return carry; }
}
