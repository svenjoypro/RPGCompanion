package com.mpvreeken.rpgcompanion.Dice;

/**
 * Created by Sven on 1/1/2018.
 */

public class Dice {

    public String label, roll;
    public int id;

    public Dice(String id, String label, String roll) {
        this.id=Integer.valueOf(id);
        this.label=label;
        this.roll=roll;
    }
}
