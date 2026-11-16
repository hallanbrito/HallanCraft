package br.com.hallancraft.torredocaos.domain;

/** JDK-only check for offline labs. Not a replacement for JUnit. */
public final class RunRulesManualCheck {
    public static void main(String[] args) {
        check(RunRules.canTransition(RunState.LOBBY, RunState.FLOOR_PREPARING), "enter run");
        check(RunRules.canTransition(RunState.WAVE_COMBAT, RunState.WAVE_INTERMISSION), "win wave");
        check(RunRules.canTransition(RunState.WAVE_COMBAT, RunState.FLOOR_TRANSITION), "win floor");
        check(!RunRules.canTransition(RunState.LOBBY, RunState.WAVE_COMBAT), "skip preparation forbidden");
        check(!RunRules.endsFloor(2, 3), "wave two must not finish floor");
        check(RunRules.endsFloor(3, 3), "wave three finishes floor");
        System.out.println("6/6 JDK domain checks PASS");
    }

    private static void check(boolean result, String name) {
        if (!result) throw new AssertionError("Failed: " + name);
    }
}
