package com.ubo.debug;

public class JDISimpleDebuggee {
    private int instanceVar1 = 10;
    private String instanceVar2 = "Hello";

    public static void main(String[] args) {
        String description = "Simple power printer";
        System.out.println(description + " -- starting");
        int x = 40;
        int power = 2;
        JDISimpleDebuggee debuggee = new JDISimpleDebuggee();
        debuggee.printPowerInstance(x, power);
    }

    public static double power(int x, int power) {
        double powerX = Math.pow(x, power);
        return powerX;
    }

    public static void printPower(int x, int power) {
        double powerX = power(x, power);
        System.out.println(powerX);
    }

    public void printPowerInstance(int x, int power) {
        double powerX = power(x, power);
        System.out.println("Instance method: " + powerX);
    }
}