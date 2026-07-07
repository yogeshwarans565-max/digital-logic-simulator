package com.dls.circuits;

import com.dls.core.Component;
import com.dls.core.Pin;

import java.util.ArrayList;
import java.util.List;

/**
 * A Component built out of other components wired together (e.g. a full
 * adder built from gates, or an N-bit register built from flip-flops).
 *
 * Subclasses build their internal wiring in the constructor, then expose
 * selected internal pins as their own inputs/outputs via {@link #exposeInput}
 * and {@link #exposeOutput}. evaluate() simply re-evaluates internal parts in
 * the order they were added, which is correct for acyclic (combinational)
 * sub-graphs; for circuits with feedback, drive them through the Simulator
 * instead so delayed propagation resolves the loop.
 */
public abstract class CompositeCircuit extends Component {

    private final List<Component> internalComponents = new ArrayList<>();

    protected CompositeCircuit(String name) {
        super(name);
    }

    protected <T extends Component> T add(T component) {
        internalComponents.add(component);
        return component;
    }

    /**
     * Exposes a new external input pin that forwards its value into one or
     * more internal pins each time evaluate() runs (supports fan-out to
     * multiple internal gates from a single external input).
     */
    protected Pin exposeInput(String exposedName, Pin... internalInputPins) {
        Pin external = addInput(exposedName);
        for (Pin internal : internalInputPins) {
            forwardedInputs.add(new Forward(external, internal));
        }
        return external;
    }

    /** Adds another internal pin that an already-exposed external input should also drive. */
    protected void fanOutInput(Pin external, Pin internalInputPin) {
        forwardedInputs.add(new Forward(external, internalInputPin));
    }

    /** Exposes an internal component's output pin as one of this circuit's own outputs. */
    protected Pin exposeOutput(String exposedName, Pin internalOutputPin) {
        Pin external = addOutput(exposedName);
        forwardedOutputs.add(new Forward(internalOutputPin, external));
        return external;
    }

    private static class Forward {
        final Pin from, to;
        Forward(Pin from, Pin to) {
            this.from = from;
            this.to = to;
        }
    }

    private final List<Forward> forwardedInputs = new ArrayList<>();
    private final List<Forward> forwardedOutputs = new ArrayList<>();

    @Override
    public void evaluate() {
        for (Forward f : forwardedInputs) {
            f.to.setValue(f.from.getValue());
        }
        for (Component c : internalComponents) {
            c.evaluate();
        }
        for (Forward f : forwardedOutputs) {
            f.to.setValue(f.from.getValue());
        }
    }

    protected List<Component> getInternalComponents() {
        return internalComponents;
    }
}
