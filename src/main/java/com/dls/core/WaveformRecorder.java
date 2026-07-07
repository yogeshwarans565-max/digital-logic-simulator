package com.dls.core;

import java.util.*;

/**
 * Records the value of a set of "watched" pins over simulation time,
 * producing the data used to render ASCII or graphical waveforms.
 */
public class WaveformRecorder {

    public static class Sample {
        public final int time;
        public final Signal value;
        public Sample(int time, Signal value) {
            this.time = time;
            this.value = value;
        }
    }

    private final Set<Pin> watched = new LinkedHashSet<>();
    private final Map<Pin, List<Sample>> history = new LinkedHashMap<>();

    public void watch(Pin pin) {
        watched.add(pin);
        history.computeIfAbsent(pin, p -> new ArrayList<>());
    }

    public void watch(Pin... pins) {
        for (Pin p : pins) watch(p);
    }

    void record(int time, Pin pin) {
        if (!watched.contains(pin)) return;
        List<Sample> samples = history.get(pin);
        // Avoid duplicate consecutive samples at the same time for the same value.
        if (!samples.isEmpty()) {
            Sample last = samples.get(samples.size() - 1);
            if (last.time == time && last.value == pin.getValue()) return;
        }
        samples.add(new Sample(time, pin.getValue()));
    }

    public List<Sample> getHistory(Pin pin) {
        return history.getOrDefault(pin, Collections.emptyList());
    }

    public Set<Pin> getWatchedPins() {
        return watched;
    }

    /**
     * Renders all watched pins as an ASCII waveform, one row per pin,
     * from time 0 up to (and including) endTime.
     */
    public String renderAscii(int endTime) {
        StringBuilder sb = new StringBuilder();
        int nameWidth = 0;
        for (Pin p : watched) {
            nameWidth = Math.max(nameWidth, (p.getOwner().getName() + "." + p.getName()).length());
        }
        for (Pin p : watched) {
            String label = p.getOwner().getName() + "." + p.getName();
            sb.append(String.format("%-" + nameWidth + "s | ", label));
            List<Sample> samples = history.get(p);
            Signal current = Signal.UNKNOWN;
            int sampleIdx = 0;
            for (int t = 0; t <= endTime; t++) {
                while (sampleIdx < samples.size() && samples.get(sampleIdx).time <= t) {
                    current = samples.get(sampleIdx).value;
                    sampleIdx++;
                }
                sb.append(current == Signal.HIGH ? "‾" : current == Signal.LOW ? "_" : "x");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
