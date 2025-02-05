package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

public class SenderCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        //debugger.printSender();
        System.out.println("Not directly available in JDI");
    }
}