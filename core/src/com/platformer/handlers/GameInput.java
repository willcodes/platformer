package com.platformer.handlers;

public class GameInput {
    public static boolean[] keys;
    public static boolean[] pkeys;

    public static final int NUM_KEYS = 4;
    public static final int JUMP = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int DOWN = 3;



    static {
        keys = new boolean[NUM_KEYS];
        pkeys = new boolean[NUM_KEYS];
    }

    public static void update() {
        for(int i = 0; i < NUM_KEYS; i++) {
            pkeys[i] = keys[i];
        }
    }

    public static boolean isDown(int i) {
        return keys[i];
    }

    public static boolean isPressed(int i) {
        return keys[i] && !pkeys[i];
    }

    public static void setKey(int i, boolean b) { keys[i] = b;  }


}
