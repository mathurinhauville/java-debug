package com.ubo.debug.commands;

import com.ubo.debug.BreakpointManager;
import com.ubo.debug.ScriptableDebugger;
import com.sun.jdi.*;

import java.util.Scanner;
import java.util.List;

/**
 * Place un breakpoint au début de l'exécution de la méthode spécifiée.
 */
public class BreakBeforeMethodCallCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) {
        BreakpointManager breakpointManager = debugger.getBreakpointManager();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter method name: ");
        String methodName = scanner.nextLine().trim();

        VirtualMachine vm = debugger.getVm();
        boolean breakpointSet = false;

        for (ReferenceType referenceType : vm.allClasses()) {

            // Parcourt toutes les méthodes de la classe
            for (Method method : referenceType.methods()) {

                // Si le nom de la méthode correspond à celui spécifié
                if (method.name().equals(methodName)) {
                    try {
                        List<Location> locations = method.allLineLocations();

                        // Place un breakpoint à la première ligne de la méthode
                        if (!locations.isEmpty()) {
                            Location methodStart = locations.getFirst();
                            breakpointManager.setBreakpoint(referenceType.name(), methodStart.lineNumber());
                            System.out.println("Breakpoint set at " + referenceType.name() + ":" + methodStart.lineNumber());
                            breakpointSet = true;
                            break;
                        }
                    } catch (AbsentInformationException e) {
                        System.out.println("No debug information available for method: " + methodName);
                    }
                }
            }
        }

        if (!breakpointSet) {
            System.out.println("Method '" + methodName + "' not found in loaded classes.");
        }
    }
}