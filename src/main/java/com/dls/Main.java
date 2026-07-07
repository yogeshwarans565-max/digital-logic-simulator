package com.dls;

import com.dls.circuits.*;
import com.dls.core.*;
import com.dls.gates.*;
import com.dls.util.TruthTableGenerator;

/**
 * Command-line demo/driver for the Digital Logic Simulator.
 * Run with no arguments to see a menu of demonstrations, or pass one of:
 *   gates      - truth tables for all primitive gates
 *   adder      - 4-bit ripple carry adder demo with sample additions
 *   flipflop   - D flip-flop clocked simulation with waveform output
 *   mux        - 2-to-1 multiplexer truth table
 */
public class Main {

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "all";

        switch (mode) {
            case "gates":
                demoGateTruthTables();
                break;
            case "adder":
                demoRippleCarryAdder();
                break;
            case "flipflop":
                demoFlipFlopWaveform();
                break;
            case "mux":
                demoMux();
                break;
            default:
                demoGateTruthTables();
                System.out.println();
                demoMux();
                System.out.println();
                demoRippleCarryAdder();
                System.out.println();
                demoFlipFlopWaveform();
        }
    }

    private static void demoGateTruthTables() {
        System.out.println("=== Primitive Gate Truth Tables ===");
        printTable(new AndGate("AND"));
        printTable(new OrGate("OR"));
        printTable(new NotGate("NOT"));
        printTable(new NandGate("NAND"));
        printTable(new NorGate("NOR"));
        printTable(new XorGate("XOR"));
        printTable(new XnorGate("XNOR"));
    }

    private static void printTable(Component gate) {
        System.out.println("-- " + gate.getName() + " --");
        System.out.print(TruthTableGenerator.renderAsText(gate));
        System.out.println();
    }

    private static void demoMux() {
        System.out.println("=== 2-to-1 Multiplexer Truth Table ===");
        printTable(new Mux2to1("MUX"));
    }

    private static void demoRippleCarryAdder() {
        System.out.println("=== 4-bit Ripple Carry Adder ===");
        RippleCarryAdder adder = new RippleCarryAdder("ADD4", 4);
        int[][] samples = {{3, 5}, {7, 9}, {15, 1}, {12, 4}, {0, 0}, {15, 15}};
        for (int[] pair : samples) {
            adder.setOperands(pair[0], pair[1]);
            adder.evaluate();
            System.out.printf("  %2d + %2d = %2d  (carry out = %b)%n",
                    pair[0], pair[1], adder.readSum(), adder.readCarryOut());
        }
    }

    private static void demoFlipFlopWaveform() {
        System.out.println("=== D Flip-Flop Clocked Simulation ===");
        Simulator sim = new Simulator();
        WaveformRecorder recorder = new WaveformRecorder();
        sim.attachRecorder(recorder);

        DFlipFlop dff = new DFlipFlop("DFF");
        recorder.watch(dff.clk(), dff.d(), dff.q());

        // Drive a repeating pattern: set D, then pulse the clock.
        boolean[] dPattern = {true, true, false, true, false, false, true};
        int t = 0;
        for (boolean dVal : dPattern) {
            sim.drive(dff.d(), Signal.fromBoolean(dVal));
            sim.drive(dff.clk(), Signal.LOW);
            sim.drive(dff.clk(), Signal.HIGH); // rising edge captures D
            sim.tick(1);
        }

        System.out.println(recorder.renderAscii(sim.getCurrentTime()));
        System.out.println("(‾ = HIGH, _ = LOW, x = unknown)");
    }
}
