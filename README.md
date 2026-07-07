# Digital Logic Simulator

**[Live demo →](https://yogeshwarans565-max.github.io/digital-logic-simulator/)**

An event-driven digital logic simulator written in pure Java (no external
dependencies). It models gates, wires, and composite circuits the same way
real HDL simulators do: components react to input changes, propagate signals
along wires with configurable delay, and a scheduler processes everything in
timestamp order.

The live demo above is a browser-based playground (`docs/index.html`, plain
HTML/JS, no dependencies) that mirrors the same gate, adder, and flip-flop
logic as the Java code, so you can try it without installing anything.

## Features

- **Primitive gates** — AND, OR, NOT, NAND, NOR, XOR, XNOR, and Buffer, all
  supporting an arbitrary number of inputs (except NOT/Buffer).
- **Composite circuits** — Half Adder, Full Adder, an N-bit Ripple Carry
  Adder, and a 2-to-1 Multiplexer, each built by wiring together smaller
  components.
- **Sequential logic** — Positive edge-triggered D and JK flip-flops with
  asynchronous reset.
- **Event-driven simulator** — A priority-queue-based scheduler (`Simulator`)
  that propagates signal changes through wires with per-wire delay, the same
  execution model used by Verilog/VHDL simulators.
- **Truth table generation** — Exhaustively exercises any component's inputs
  and prints a formatted truth table.
- **Waveform output** — `WaveformRecorder` captures signal history over time
  and renders it as an ASCII timing diagram.

## Project Structure

```
src/main/java/com/dls/
├── core/         # Signal, Pin, Wire, Component, Simulator, WaveformRecorder
├── gates/        # Gate (base class), AndGate, OrGate, NotGate, NandGate, NorGate, XorGate, XnorGate, BufferGate
├── circuits/     # CompositeCircuit (base), HalfAdder, FullAdder, RippleCarryAdder, Mux2to1, DFlipFlop, JKFlipFlop
├── util/         # TruthTableGenerator
└── Main.java     # CLI demo entry point

src/test/java/com/dls/
└── AllTests.java # Self-contained test suite (no test framework dependency)

docs/
└── index.html    # Live browser demo (mirrors the Java logic, hosted via GitHub Pages)
```

## Architecture

Every gate and circuit extends the abstract `Component` class, which exposes
named input/output `Pin`s and a single `evaluate()` method that recomputes
outputs from current inputs. `Wire` connects one output pin to one input
pin. The `Simulator` drives everything: setting an input pin schedules an
event; processing that event re-evaluates the owning component and schedules
further events for anything the changed outputs drive, respecting each
wire's delay. This event-driven design is what allows accurate waveform
timing and correctly resolves circuits with feedback (like the toggling JK
flip-flop).

Composite circuits (`CompositeCircuit`) are built by instantiating smaller
components in the constructor and exposing selected internal pins as the
circuit's own inputs/outputs — so a `FullAdder` is genuinely built from two
`HalfAdder`s and an `OrGate`, and an N-bit `RippleCarryAdder` is built by
chaining `FullAdder` stages, mirroring how these circuits are taught.

## Getting Started

### Prerequisites

- JDK 17 or later

### Build & Run (JDK only, no Maven required)

```bash
./scripts/build_and_test.sh
```

This compiles everything, runs the test suite, and then runs the CLI demo.

### Build & Run with Maven

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.dls.Main"
```

### Run specific demos

```bash
java -cp out com.dls.Main gates      # truth tables for all primitive gates
java -cp out com.dls.Main mux        # 2-to-1 multiplexer truth table
java -cp out com.dls.Main adder      # 4-bit ripple carry adder demo
java -cp out com.dls.Main flipflop   # D flip-flop clocked simulation + waveform
```

## Example Output

**Truth table (XOR gate):**

```
A0  A1  | Y
------------
0   0   | 0
1   0   | 1
0   1   | 1
1   1   | 0
```

**4-bit ripple carry adder:**

```
 3 +  5 =  8  (carry out = false)
 7 +  9 = 16  ... wraps to 0 (carry out = true)  // 4 bits max = 15
```

**Waveform (D flip-flop):**

```
DFF.CLK | _‾_‾_‾_‾_‾_‾_‾
DFF.D   | ‾‾__‾___‾
DFF.Q   | _‾‾__‾___
```

## Extending the Simulator

To add a new gate: extend `Gate`, implement `compute(Signal[] inputs)`.
To add a new composite circuit: extend `CompositeCircuit`, wire up
sub-components in the constructor with `add(...)`, and expose pins via
`exposeInput`/`exposeOutput`. To add a new sequential element (like a T
flip-flop or a register), extend `Component` directly and manage state in
your own `evaluate()`, following the pattern in `DFlipFlop`/`JKFlipFlop`.

## Running Tests

```bash
./scripts/build_and_test.sh
```

`AllTests.java` is a small, dependency-free test runner: it exercises every
gate's truth table, verifies the half/full/ripple-carry adders against known
sums (including overflow), checks flip-flop edge-triggering, and verifies
the simulator's wire propagation and waveform recording.

## License

MIT — see [LICENSE](LICENSE).
