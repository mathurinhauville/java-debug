package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.ubo.debug.BreakpointManager;
import com.ubo.debug.ScriptableDebugger;

import java.util.Scanner;

/**
 * Installe un point d’arrêt à la ligne lineNumber de la classe className.
 * Ce point d’arrêt ne s’active qu’après avoir été atteint un certain nombre de fois count.
 */
public class BreakOnCountCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws AbsentInformationException {
        BreakpointManager breakpointManager = debugger.getBreakpointManager();

        breakpointManager.setBreakpoint("com.ubo.debug.JDISimpleDebuggee", 10);
        System.out.print("Enter hit count before activation: ");
        Scanner scanner = new Scanner(System.in);
        scanner.nextInt();
    }
}