package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

import java.util.Scanner;

public class PrintVarCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        System.out.print("Enter variable name: ");
        Scanner scanner = new Scanner(System.in);
        String varName = scanner.nextLine().trim();
        try {
            debugger.printVariable(varName);
        } catch (Exception e) {
            System.out.println("Error printing variable: " + e.getMessage());
        }
    }
}