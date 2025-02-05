package com.ubo.debug.commands;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VirtualMachine;
import com.ubo.debug.ScriptableDebugger;

import java.util.Scanner;

public class PrintVarCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        VirtualMachine vm = debugger.getVm();

        System.out.print("Enter variable name: ");
        Scanner scanner = new Scanner(System.in);
        String varName = scanner.nextLine().trim();
        try {
            StackFrame frame = vm.allThreads().getFirst().frame(0);
            LocalVariable var = frame.visibleVariableByName(varName);
            System.out.println(var.name() + " -> " + frame.getValue(var));
        } catch (Exception e) {
            System.out.println("Error printing variable: " + e.getMessage());
        }
    }
}