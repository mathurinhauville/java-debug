package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

/**
 * Affiche la valeur du compteur de programme.
 */
public class PcCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) {
        System.out.println("PC = " + debugger.getPC());
    }
}
