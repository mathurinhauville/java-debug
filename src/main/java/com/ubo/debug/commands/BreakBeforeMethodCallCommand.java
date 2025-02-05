package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

import java.util.Scanner;

public class BreakBeforeMethodCallCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        System.out.print("Enter method name: ");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Not natively supported by JDI");
    }
}