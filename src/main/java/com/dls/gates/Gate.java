package com.dls.gates;

import com.dls.core.Component;
import com.dls.core.Pin;
import com.dls.core.Signal;

/**
 * Base class for primitive gates with a variable number of inputs ("A0", "A1", ...)
 * and a single output ("Y"). Subclasses just implement the boolean function.
 */
public abstract class Gate extends Component {

    protected final Pin[] inputPins;
    protected final Pin outputPin;

    protected Gate(String name, int numInputs) {
        super(name);
        inputPins = new Pin[numInputs];
        for (int i = 0; i < numInputs; i++) {
            inputPins[i] = addInput("A" + i);
        }
        outputPin = addOutput("Y");
    }

    public Pin in(int index) {
        return inputPins[index];
    }

    public Pin out() {
        return outputPin;
    }

    @Override
    public void evaluate() {
        Signal[] values = new Signal[inputPins.length];
        for (int i = 0; i < inputPins.length; i++) {
            values[i] = inputPins[i].getValue();
        }
        outputPin.setValue(compute(values));
    }

    /** The gate's boolean function over its current input values. */
    protected abstract Signal compute(Signal[] inputs);
}
