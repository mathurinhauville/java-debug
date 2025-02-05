package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.ubo.debug.BreakpointManager;
import com.ubo.debug.ScriptableDebugger;

import java.util.Scanner;

/**
 * Installe un point d’arrêt à la ligne lineNumber de la classe className.
 */
public class BreakCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws AbsentInformationException {
        BreakpointManager breakpointManager = debugger.getBreakpointManager();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter class name: ");
        String className = scanner.nextLine().trim();

        System.out.print("Enter line number: ");
        int lineNumber = scanner.nextInt();

        // Ajoute le breakpoint
        breakpointManager.setBreakpoint(className, lineNumber);
        // Sauvegarde le breakpoint dans un fichier pour le retrouver lors de la prochaine exécution
        breakpointManager.saveBreakpointToFile(className, lineNumber);
    }
}