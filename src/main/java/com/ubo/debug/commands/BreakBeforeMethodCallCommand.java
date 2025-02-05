package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

import java.util.Scanner;

/**
 * Configure l’exécution pour s’arrêter au tout début de l’exécution de la méthode methodName.
 */
public class BreakBeforeMethodCallCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) {
        System.out.println("Not natively supported by JDI");
    }
}