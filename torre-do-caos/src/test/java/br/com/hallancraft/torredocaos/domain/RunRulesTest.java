package br.com.hallancraft.torredocaos.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RunRulesTest {
    @Test void allowsExpectedTransitions() {
        assertTrue(RunRules.canTransition(RunState.LOBBY, RunState.FLOOR_PREPARING));
        assertTrue(RunRules.canTransition(RunState.WAVE_COMBAT, RunState.WAVE_INTERMISSION));
        assertTrue(RunRules.canTransition(RunState.WAVE_COMBAT, RunState.FLOOR_TRANSITION));
        assertTrue(RunRules.canTransition(RunState.RUN_DEFEAT_CLEANUP, RunState.LOBBY));
    }

    @Test void refusesUnsafeTransitions() {
        assertThrows(IllegalStateException.class, () ->
            RunRules.requireTransition(RunState.LOBBY, RunState.WAVE_COMBAT));
        assertFalse(RunRules.canTransition(RunState.RUN_DEFEAT_CLEANUP, RunState.WAVE_COMBAT));
    }

    @Test void floorFinishesOnThirdWave() {
        assertFalse(RunRules.endsFloor(1, 3));
        assertFalse(RunRules.endsFloor(2, 3));
        assertTrue(RunRules.endsFloor(3, 3));
        assertThrows(IllegalArgumentException.class, () -> RunRules.endsFloor(4, 3));
    }
}
