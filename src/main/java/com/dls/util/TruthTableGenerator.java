package com.dls.util;

import com.dls.core.Component;
import com.dls.core.Pin;
import com.dls.core.Signal;

import java.util.*;

/**
 * Exhaustively exercises every combination of a component's input pins and
 * records the resulting outputs, producing a full truth table. Practical
 * for components with a modest number of inputs (2^n combinations).
 */
public class TruthTableGenerator {

    public static class Row {
        public final Map<String, Signal> inputs;
        public final Map<String, Signal> outputs;
        Row(Map<String, Signal> inputs, Map<String, Signal> outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
        }
    }

    /**
     * Generates the full truth table for the given component by directly
     * setting its input pins and calling evaluate() (bypassing the
     * Simulator's event queue, since we want an instantaneous combinational
     * read for each row, not delayed propagation).
     */
    public static List<Row> generate(Component component) {
        List<Pin> inputs = new ArrayList<>(component.getInputs().values());
        List<Pin> outputs = new ArrayList<>(component.getOutputs().values());
        List<Row> rows = new ArrayList<>();

        int n = inputs.size();
        int combinations = 1 << n;
        for (int mask = 0; mask < combinations; mask++) {
            Map<String, Signal> inputAssignment = new LinkedHashMap<>();
            for (int i = 0; i < n; i++) {
                boolean bit = ((mask >> i) & 1) == 1;
                Signal s = Signal.fromBoolean(bit);
                inputs.get(i).setValue(s);
                inputAssignment.put(inputs.get(i).getName(), s);
            }
            component.evaluate();
            Map<String, Signal> outputAssignment = new LinkedHashMap<>();
            for (Pin out : outputs) {
                outputAssignment.put(out.getName(), out.getValue());
            }
            rows.add(new Row(inputAssignment, outputAssignment));
        }
        return rows;
    }

    /** Renders a truth table as a formatted text table, e.g. for console or file output. */
    public static String renderAsText(Component component) {
        List<Row> rows = generate(component);
        if (rows.isEmpty()) return "(no inputs)";

        List<String> inputNames = new ArrayList<>(rows.get(0).inputs.keySet());
        List<String> outputNames = new ArrayList<>(rows.get(0).outputs.keySet());

        StringBuilder sb = new StringBuilder();
        for (String in : inputNames) sb.append(String.format("%-4s", in));
        sb.append("| ");
        for (String out : outputNames) sb.append(String.format("%-4s", out));
        sb.append('\n');
        sb.append("-".repeat(inputNames.size() * 4 + 2 + outputNames.size() * 4)).append('\n');

        for (Row row : rows) {
            for (String in : inputNames) sb.append(String.format("%-4s", row.inputs.get(in)));
            sb.append("| ");
            for (String out : outputNames) sb.append(String.format("%-4s", row.outputs.get(out)));
            sb.append('\n');
        }
        return sb.toString();
    }
}
