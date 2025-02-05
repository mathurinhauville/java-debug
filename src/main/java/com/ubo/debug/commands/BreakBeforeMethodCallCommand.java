package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

import java.util.Scanner;

public class BreakBeforeMethodCallCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        //debugger.setMethodCallBreakpoint();
        System.out.print("Enter method name: ");
        Scanner scanner = new Scanner(System.in);
        String methodName = scanner.nextLine().trim();
        System.out.println("Not natively supported by JDI");
    }
}