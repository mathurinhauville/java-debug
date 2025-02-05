package com.ubo.debug.commands;

import com.ubo.debug.ScriptableDebugger;

public class SenderCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        System.out.println("Not directly available in JDI");
    }
}