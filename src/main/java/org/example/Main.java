package org.example;

import org.example.businesslogic.SimulationManager;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
    SimulationManager gen = new SimulationManager();
    Thread t = new Thread(gen);
    t.start();
    }
}

