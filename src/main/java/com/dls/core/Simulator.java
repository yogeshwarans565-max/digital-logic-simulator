package com.dls.core;

import java.util.*;

/**
 * Event-driven digital logic simulator.
 *
 * Design: a priority queue of scheduled events ordered by simulation time.
 * Setting a pin's value schedules an event; processing an event evaluates
 * the affected component(s) and, if their outputs change, schedules further
 * events on the wires those outputs drive (respecting wire delay). This is
 * the same basic model used by real HDL simulators (VHDL/Verilog).
 *
 * A WaveformRecorder can be attached to capture the value of chosen pins at
 * every simulated tick, which is what powers the waveform viewer.
 */
public class Simulator {

    private static class Event implements Comparable<Event> {
        final int time;
        final Pin pin;
        final Signal value;

        Event(int time, Pin pin, Signal value) {
            this.time = time;
            this.pin = pin;
            this.value = value;
        }

        @Override
        public int compareTo(Event o) {
            return Integer.compare(this.time, o.time);
        }
    }

    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();
    private int currentTime = 0;
    private WaveformRecorder recorder;

    public void attachRecorder(WaveformRecorder recorder) {
        this.recorder = recorder;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * Immediately drives an input pin (e.g. a switch or clock) to a value at
     * the current simulation time, then runs the queue to quiescence.
     */
    public void drive(Pin pin, Signal value) {
        scheduleAndRun(currentTime, pin, value);
    }

    private void scheduleAndRun(int time, Pin pin, Signal value) {
        eventQueue.add(new Event(time, pin, value));
        run();
    }

    /** Processes all pending events in time order until the queue is empty. */
    public void run() {
        while (!eventQueue.isEmpty()) {
            Event ev = eventQueue.poll();
            currentTime = ev.time;

            boolean changed = ev.pin.setValue(ev.value);
            recordIfNeeded(ev.pin);

            if (!changed) continue;

            if (ev.pin.getDirection() == Pin.Direction.INPUT) {
                // Re-evaluate the owning component; its outputs may change.
                Component owner = ev.pin.getOwner();
                owner.evaluate();
                for (Pin out : owner.getOutputs().values()) {
                    propagate(out);
                }
            } else {
                // An output was set directly (e.g. primary output binding); propagate.
                propagate(ev.pin);
            }
        }
    }

    /** Schedules delayed events on every wire driven by the given output pin. */
    private void propagate(Pin outputPin) {
        recordIfNeeded(outputPin);
        for (Wire w : outputPin.getConnectedWires()) {
            if (w.getSource() != outputPin) continue; // this pin is the target on this wire
            int arrival = currentTime + Math.max(w.getDelay(), 1);
            eventQueue.add(new Event(arrival, w.getTarget(), outputPin.getValue()));
        }
    }

    private void recordIfNeeded(Pin pin) {
        if (recorder != null) {
            recorder.record(currentTime, pin);
        }
    }

    /** Advances simulated time by n ticks without changing any inputs (useful for clocks). */
    public void tick(int n) {
        currentTime += n;
    }
}
