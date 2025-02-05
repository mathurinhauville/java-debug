package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.ubo.debug.BreakpointManager;
import com.ubo.debug.ScriptableDebugger;

import java.util.Scanner;

public class BreakOnCountCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws AbsentInformationException {
        //debugger.setConditionalBreakpoint();
        BreakpointManager breakpointManager = debugger.getBreakpointManager();
        breakpointManager.setBreakpoint("com.ubo.debug.JDISimpleDebuggee", 10); // Example arguments
        System.out.print("Enter hit count before activation: ");
        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
    }
}