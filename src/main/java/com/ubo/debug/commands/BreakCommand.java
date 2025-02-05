package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.ubo.debug.ScriptableDebugger;
import java.util.Scanner;

public class BreakCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws AbsentInformationException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter class name: ");
        String className = scanner.nextLine().trim();
        System.out.print("Enter line number: ");
        int lineNumber = scanner.nextInt();
        debugger.setBreakpoint(className, lineNumber);
    }
}