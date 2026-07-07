package com.dls.circuits;

import com.dls.core.Component;
import com.dls.core.Pin;
import com.dls.core.Signal;

/**
 * Positive edge-triggered D flip-flop with asynchronous active-high reset.
 * Inputs: D (data), CLK (clock), RST (async reset)
 * Outputs: Q, QN (complement)
 */
public class DFlipFlop extends Component {

    private final Pin d, clk, rst, q, qn;
    private Signal lastClk = Signal.LOW;
    private Signal state = Signal.LOW;

    public DFlipFlop(String name) {
        super(name);
        d = addInput("D");
        clk = addInput("CLK");
        rst = addInput("RST");
        q = addOutput("Q");
        qn = addOutput("QN");
    }

    public Pin d() { return d; }
    public Pin clk() { return clk; }
    public Pin rst() { return rst; }
    public Pin q() { return q; }
    public Pin qn() { return qn; }

    @Override
    public void evaluate() {
        if (rst.getValue() == Signal.HIGH) {
            state = Signal.LOW;
        } else {
            Signal currentClk = clk.getValue();
            boolean risingEdge = lastClk != Signal.HIGH && currentClk == Signal.HIGH;
            if (risingEdge && d.getValue().isDefined()) {
                state = d.getValue();
            }
            lastClk = currentClk;
        }
        q.setValue(state);
        qn.setValue(state.not());
    }
}
