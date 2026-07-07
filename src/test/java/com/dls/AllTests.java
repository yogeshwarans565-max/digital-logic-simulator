package com.dls;

import com.dls.circuits.*;
import com.dls.core.*;
import com.dls.gates.*;
import com.dls.util.TruthTableGenerator;

import java.util.List;

/**
 * Minimal self-contained test suite (no JUnit dependency required, so it
 * runs anywhere a JDK is available with zero setup: just compile and run
 * this class's main method). Each test method throws an AssertionError with
 * a descriptive message on failure; main() reports a pass/fail summary and
 * exits with a non-zero code if anything failed, so it also works in CI.
 */
public class AllTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("AND gate truth table", AllTests::testAndGate);
        run("OR gate truth table", AllTests::testOrGate);
        run("NOT gate", AllTests::testNotGate);
        run("NAND gate", AllTests::testNandGate);
        run("NOR gate", AllTests::testNorGate);
        run("XOR gate", AllTests::testXorGate);
        run("XNOR gate", AllTests::testXnorGate);
        run("Half adder", AllTests::testHalfAdder);
        run("Full adder", AllTests::testFullAdder);
        run("4-bit ripple carry adder (no overflow)", AllTests::testRippleAdderNoOverflow);
        run("4-bit ripple carry adder (overflow)", AllTests::testRippleAdderOverflow);
        run("2-to-1 multiplexer", AllTests::testMux);
        run("D flip-flop captures on rising edge only", AllTests::testDFlipFlop);
        run("JK flip-flop toggle mode", AllTests::testJKFlipFlopToggle);
        run("Simulator propagates through wires with delay", AllTests::testSimulatorWirePropagation);
        run("Waveform recorder captures transitions", AllTests::testWaveformRecorder);

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private interface TestCase {
        void run() throws Exception;
    }

    private static void run(String name, TestCase test) {
        try {
            test.run();
            System.out.println("[PASS] " + name);
            passed++;
        } catch (Throwable t) {
            System.out.println("[FAIL] " + name + " -> " + t.getMessage());
            failed++;
        }
    }

    private static void assertEquals(Object expected, Object actual, String context) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(context + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertTrue(boolean condition, String context) {
        if (!condition) {
            throw new AssertionError(context);
        }
    }

    // ---------- Gate tests ----------

    private static void testAndGate() {
        AndGate g = new AndGate("AND");
        g.in(0).setValue(Signal.HIGH);
        g.in(1).setValue(Signal.HIGH);
        g.evaluate();
        assertEquals(Signal.HIGH, g.out().getValue(), "1 AND 1");

        g.in(1).setValue(Signal.LOW);
        g.evaluate();
        assertEquals(Signal.LOW, g.out().getValue(), "1 AND 0");
    }

    private static void testOrGate() {
        OrGate g = new OrGate("OR");
        g.in(0).setValue(Signal.LOW);
        g.in(1).setValue(Signal.LOW);
        g.evaluate();
        assertEquals(Signal.LOW, g.out().getValue(), "0 OR 0");

        g.in(0).setValue(Signal.HIGH);
        g.evaluate();
        assertEquals(Signal.HIGH, g.out().getValue(), "1 OR 0");
    }

    private static void testNotGate() {
        NotGate g = new NotGate("NOT");
        g.in(0).setValue(Signal.LOW);
        g.evaluate();
        assertEquals(Signal.HIGH, g.out().getValue(), "NOT 0");

        g.in(0).setValue(Signal.HIGH);
        g.evaluate();
        assertEquals(Signal.LOW, g.out().getValue(), "NOT 1");
    }

    private static void testNandGate() {
        NandGate g = new NandGate("NAND");
        g.in(0).setValue(Signal.HIGH);
        g.in(1).setValue(Signal.HIGH);
        g.evaluate();
        assertEquals(Signal.LOW, g.out().getValue(), "1 NAND 1");

        g.in(1).setValue(Signal.LOW);
        g.evaluate();
        assertEquals(Signal.HIGH, g.out().getValue(), "1 NAND 0");
    }

    private static void testNorGate() {
        NorGate g = new NorGate("NOR");
        g.in(0).setValue(Signal.LOW);
        g.in(1).setValue(Signal.LOW);
        g.evaluate();
        assertEquals(Signal.HIGH, g.out().getValue(), "0 NOR 0");

        g.in(0).setValue(Signal.HIGH);
        g.evaluate();
        assertEquals(Signal.LOW, g.out().getValue(), "1 NOR 0");
    }

    private static void testXorGate() {
        XorGate g = new XorGate("XOR");
        g.in(0).setValue(Signal.HIGH);
        g.in(1).setValue(Signal.LOW);
        g.evaluate();
        assertEquals(Signal.HIGH, g.out().getValue(), "1 XOR 0");

        g.in(1).setValue(Signal.HIGH);
        g.evaluate();
        assertEquals(Signal.LOW, g.out().getValue(), "1 XOR 1");
    }

    private static void testXnorGate() {
        XnorGate g = new XnorGate("XNOR");
        g.in(0).setValue(Signal.HIGH);
        g.in(1).setValue(Signal.HIGH);
        g.evaluate();
        assertEquals(Signal.HIGH, g.out().getValue(), "1 XNOR 1");

        g.in(1).setValue(Signal.LOW);
        g.evaluate();
        assertEquals(Signal.LOW, g.out().getValue(), "1 XNOR 0");
    }

    // ---------- Circuit tests ----------

    private static void testHalfAdder() {
        HalfAdder ha = new HalfAdder("HA");
        ha.a().setValue(Signal.HIGH);
        ha.b().setValue(Signal.HIGH);
        ha.evaluate();
        assertEquals(Signal.LOW, ha.sum().getValue(), "1+1 sum");
        assertEquals(Signal.HIGH, ha.carry().getValue(), "1+1 carry");

        ha.a().setValue(Signal.HIGH);
        ha.b().setValue(Signal.LOW);
        ha.evaluate();
        assertEquals(Signal.HIGH, ha.sum().getValue(), "1+0 sum");
        assertEquals(Signal.LOW, ha.carry().getValue(), "1+0 carry");
    }

    private static void testFullAdder() {
        FullAdder fa = new FullAdder("FA");
        fa.a().setValue(Signal.HIGH);
        fa.b().setValue(Signal.HIGH);
        fa.cin().setValue(Signal.HIGH);
        fa.evaluate();
        // 1+1+1 = 3 = 0b11 -> sum=1, cout=1
        assertEquals(Signal.HIGH, fa.sum().getValue(), "1+1+1 sum");
        assertEquals(Signal.HIGH, fa.cout().getValue(), "1+1+1 cout");

        fa.a().setValue(Signal.LOW);
        fa.b().setValue(Signal.LOW);
        fa.cin().setValue(Signal.LOW);
        fa.evaluate();
        assertEquals(Signal.LOW, fa.sum().getValue(), "0+0+0 sum");
        assertEquals(Signal.LOW, fa.cout().getValue(), "0+0+0 cout");
    }

    private static void testRippleAdderNoOverflow() {
        RippleCarryAdder adder = new RippleCarryAdder("ADD4", 4);
        adder.setOperands(3, 5);
        adder.evaluate();
        assertEquals(8, adder.readSum(), "3+5 sum");
        assertEquals(false, adder.readCarryOut(), "3+5 carry out");
    }

    private static void testRippleAdderOverflow() {
        RippleCarryAdder adder = new RippleCarryAdder("ADD4", 4);
        adder.setOperands(9, 7); // 16 overflows 4 bits -> 0, carry 1
        adder.evaluate();
        assertEquals(0, adder.readSum(), "9+7 sum (overflowed)");
        assertEquals(true, adder.readCarryOut(), "9+7 carry out");
    }

    private static void testMux() {
        Mux2to1 mux = new Mux2to1("MUX");
        mux.a().setValue(Signal.HIGH);
        mux.b().setValue(Signal.LOW);
        mux.sel().setValue(Signal.LOW);
        mux.evaluate();
        assertEquals(Signal.HIGH, mux.y().getValue(), "sel=0 selects A");

        mux.sel().setValue(Signal.HIGH);
        mux.evaluate();
        assertEquals(Signal.LOW, mux.y().getValue(), "sel=1 selects B");
    }

    private static void testDFlipFlop() {
        DFlipFlop dff = new DFlipFlop("DFF");
        dff.rst().setValue(Signal.LOW);
        dff.clk().setValue(Signal.LOW);
        dff.d().setValue(Signal.HIGH);
        dff.evaluate(); // no clock edge yet
        assertEquals(Signal.LOW, dff.q().getValue(), "Q before clock edge stays LOW");

        dff.clk().setValue(Signal.HIGH); // rising edge
        dff.evaluate();
        assertEquals(Signal.HIGH, dff.q().getValue(), "Q captures D on rising edge");

        dff.d().setValue(Signal.LOW); // change D without a new edge
        dff.evaluate();
        assertEquals(Signal.HIGH, dff.q().getValue(), "Q holds value without new clock edge");
    }

    private static void testJKFlipFlopToggle() {
        JKFlipFlop jk = new JKFlipFlop("JK");
        jk.rst().setValue(Signal.LOW);
        jk.clk().setValue(Signal.LOW);
        jk.j().setValue(Signal.HIGH);
        jk.k().setValue(Signal.HIGH);
        jk.evaluate();
        assertEquals(Signal.LOW, jk.q().getValue(), "initial state LOW");

        jk.clk().setValue(Signal.HIGH); // toggle
        jk.evaluate();
        assertEquals(Signal.HIGH, jk.q().getValue(), "toggled to HIGH");

        jk.clk().setValue(Signal.LOW);
        jk.evaluate();
        jk.clk().setValue(Signal.HIGH); // toggle again
        jk.evaluate();
        assertEquals(Signal.LOW, jk.q().getValue(), "toggled back to LOW");
    }

    private static void testSimulatorWirePropagation() {
        Simulator sim = new Simulator();
        NotGate inverter = new NotGate("INV");
        AndGate and = new AndGate("AND");

        Wire w = new Wire(inverter.out(), and.in(0));
        and.in(1).setValue(Signal.HIGH); // static high

        sim.drive(inverter.in(0), Signal.LOW); // NOT 0 = 1, propagates to AND.in(0)
        assertEquals(Signal.HIGH, and.in(0).getValue(), "wire propagated inverter output");

        // AND wasn't re-evaluated automatically here since AND.in(1) was set directly
        // without going through the simulator; explicitly evaluate to check logic.
        and.evaluate();
        assertEquals(Signal.HIGH, and.out().getValue(), "1 AND 1 via propagated wire");
        assertTrue(w.getDelay() == 0, "default wire delay is 0 (still resolves in >=1 tick)");
    }

    private static void testWaveformRecorder() {
        Simulator sim = new Simulator();
        WaveformRecorder recorder = new WaveformRecorder();
        sim.attachRecorder(recorder);

        NotGate inverter = new NotGate("INV");
        recorder.watch(inverter.in(0), inverter.out());

        sim.drive(inverter.in(0), Signal.LOW);
        sim.drive(inverter.in(0), Signal.HIGH);

        List<WaveformRecorder.Sample> outHistory = recorder.getHistory(inverter.out());
        assertTrue(outHistory.size() >= 2, "output recorded at least 2 transitions, got " + outHistory.size());
    }
}
