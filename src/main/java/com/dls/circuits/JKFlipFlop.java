package com.dls.circuits;

import com.dls.core.Component;
import com.dls.core.Pin;
import com.dls.core.Signal;

/**
 * Positive edge-triggered JK flip-flop.
 * J=0,K=0: hold. J=1,K=0: set. J=0,K=1: reset. J=1,K=1: toggle.
 */
public class JKFlipFlop extends Component {

    private final Pin j, k, clk, rst, q, qn;
    private Signal lastClk = Signal.LOW;
    private Signal state = Signal.LOW;

    public JKFlipFlop(String name) {
        super(name);
        j = addInput("J");
        k = addInput("K");
        clk = addInput("CLK");
        rst = addInput("RST");
        q = addOutput("Q");
        qn = addOutput("QN");
    }

    public Pin j() { return j; }
    public Pin k() { return k; }
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
            if (risingEdge && j.getValue().isDefined() && k.getValue().isDefined()) {
                boolean jj = j.getValue() == Signal.HIGH;
                boolean kk = k.getValue() == Signal.HIGH;
                if (jj && kk) {
                    state = state.not();
                } else if (jj) {
                    state = Signal.HIGH;
                } else if (kk) {
                    state = Signal.LOW;
                }
                // else hold
            }
            lastClk = currentClk;
        }
        q.setValue(state);
        qn.setValue(state.not());
    }
}
