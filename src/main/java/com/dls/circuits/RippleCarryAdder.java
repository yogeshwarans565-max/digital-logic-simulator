package com.dls.circuits;

import com.dls.core.Pin;
import com.dls.core.Signal;

/**
 * N-bit ripple-carry adder built by chaining FullAdder stages.
 * Inputs:  A0..A(n-1), B0..B(n-1), CIN
 * Outputs: S0..S(n-1), COUT
 */
public class RippleCarryAdder extends CompositeCircuit {

    private final int width;
    private final Pin[] aBits, bBits, sumBits;
    private final Pin cin, cout;
    private final FullAdder[] stages;

    public RippleCarryAdder(String name, int width) {
        super(name);
        this.width = width;
        stages = new FullAdder[width];
        aBits = new Pin[width];
        bBits = new Pin[width];
        sumBits = new Pin[width];

        for (int i = 0; i < width; i++) {
            stages[i] = add(new FullAdder(name + ".fa" + i));
            aBits[i] = exposeInput("A" + i, stages[i].a());
            bBits[i] = exposeInput("B" + i, stages[i].b());
        }
        cin = exposeInput("CIN", stages[0].cin());
        for (int i = 0; i < width; i++) {
            sumBits[i] = exposeOutput("S" + i, stages[i].sum());
        }
        cout = exposeOutput("COUT", stages[width - 1].cout());
    }

    @Override
    public void evaluate() {
        Signal carry = cin.getValue();
        for (int i = 0; i < width; i++) {
            stages[i].a().setValue(aBits[i].getValue());
            stages[i].b().setValue(bBits[i].getValue());
            stages[i].cin().setValue(carry);
            stages[i].evaluate();
            sumBits[i].setValue(stages[i].sum().getValue());
            carry = stages[i].cout().getValue();
        }
        cout.setValue(carry);
    }

    /** Convenience: drives A/B inputs from two integers (LSB-first bit0..bitN-1) and returns the sum as an int. */
    public void setOperands(int aValue, int bValue) {
        for (int i = 0; i < width; i++) {
            aBits[i].setValue(Signal.fromBoolean(((aValue >> i) & 1) == 1));
            bBits[i].setValue(Signal.fromBoolean(((bValue >> i) & 1) == 1));
        }
        cin.setValue(Signal.LOW);
    }

    public int readSum() {
        int result = 0;
        for (int i = 0; i < width; i++) {
            if (sumBits[i].getValue() == Signal.HIGH) result |= (1 << i);
        }
        return result;
    }

    public boolean readCarryOut() {
        return cout.getValue() == Signal.HIGH;
    }

    public int getWidth() { return width; }
    public Pin a(int i) { return aBits[i]; }
    public Pin b(int i) { return bBits[i]; }
    public Pin sum(int i) { return sumBits[i]; }
    public Pin cin() { return cin; }
    public Pin cout() { return cout; }
}
