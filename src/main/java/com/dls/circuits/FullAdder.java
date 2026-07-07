package com.dls.circuits;

import com.dls.core.Pin;
import com.dls.gates.OrGate;

/**
 * Full adder: adds two bits plus an incoming carry.
 * Built from two half adders and an OR gate, the textbook composition:
 *   SUM  = A XOR B XOR CIN
 *   COUT = (A AND B) OR (CIN AND (A XOR B))
 */
public class FullAdder extends CompositeCircuit {

    private final Pin a, b, cin, sum, cout;

    public FullAdder(String name) {
        super(name);
        HalfAdder ha1 = add(new HalfAdder(name + ".ha1"));
        HalfAdder ha2 = add(new HalfAdder(name + ".ha2"));
        OrGate orGate = add(new OrGate(name + ".or"));

        a = exposeInput("A", ha1.a());
        b = exposeInput("B", ha1.b());
        cin = exposeInput("CIN", ha2.b());

        sum = exposeOutput("SUM", ha2.sum());
        cout = exposeOutput("COUT", orGate.out());

        this.ha1 = ha1;
        this.ha2 = ha2;
        this.orGate = orGate;
    }

    private final HalfAdder ha1, ha2;
    private final OrGate orGate;

    @Override
    public void evaluate() {
        // Forward externals into ha1
        ha1.a().setValue(a.getValue());
        ha1.b().setValue(b.getValue());
        ha1.evaluate();

        // ha1.sum feeds ha2 alongside the external carry-in
        ha2.a().setValue(ha1.sum().getValue());
        ha2.b().setValue(cin.getValue());
        ha2.evaluate();

        // cout = ha1.carry OR ha2.carry
        orGate.in(0).setValue(ha1.carry().getValue());
        orGate.in(1).setValue(ha2.carry().getValue());
        orGate.evaluate();

        sum.setValue(ha2.sum().getValue());
        cout.setValue(orGate.out().getValue());
    }

    public Pin a() { return a; }
    public Pin b() { return b; }
    public Pin cin() { return cin; }
    public Pin sum() { return sum; }
    public Pin cout() { return cout; }
}
