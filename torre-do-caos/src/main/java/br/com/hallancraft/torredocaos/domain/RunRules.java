package br.com.hallancraft.torredocaos.domain;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/** Pure domain rules, independent of Bukkit, suitable for unit tests. */
public final class RunRules {
    private static final Map<RunState, EnumSet<RunState>> ALLOWED = new EnumMap<>(RunState.class);
    static {
        ALLOWED.put(RunState.LOBBY, EnumSet.of(RunState.FLOOR_PREPARING));
        ALLOWED.put(RunState.FLOOR_PREPARING, EnumSet.of(RunState.WAVE_COMBAT, RunState.RUN_DEFEAT_CLEANUP));
        ALLOWED.put(RunState.WAVE_COMBAT, EnumSet.of(RunState.WAVE_INTERMISSION, RunState.FLOOR_TRANSITION, RunState.RUN_DEFEAT_CLEANUP));
        ALLOWED.put(RunState.WAVE_INTERMISSION, EnumSet.of(RunState.WAVE_COMBAT, RunState.RUN_DEFEAT_CLEANUP));
        ALLOWED.put(RunState.FLOOR_TRANSITION, EnumSet.of(RunState.FLOOR_PREPARING, RunState.RUN_DEFEAT_CLEANUP));
        ALLOWED.put(RunState.RUN_DEFEAT_CLEANUP, EnumSet.of(RunState.LOBBY));
    }
    private RunRules() {}

    public static boolean canTransition(RunState from, RunState to) {
        return ALLOWED.get(from).contains(to);
    }

    public static void requireTransition(RunState from, RunState to) {
        if (!canTransition(from, to)) {
            throw new IllegalStateException("Invalid run transition: " + from + " -> " + to);
        }
    }

    public static boolean endsFloor(int waveInFloor, int wavesPerFloor) {
        if (wavesPerFloor <= 0 || waveInFloor < 1 || waveInFloor > wavesPerFloor)
            throw new IllegalArgumentException("invalid wave configuration");
        return waveInFloor == wavesPerFloor;
    }
}
