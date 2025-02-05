package com.ubo.debug.commands;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.ubo.debug.ScriptableDebugger;

public class ContinueCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();
        StepCommand stepCommand = new StepCommand();

        try {
            while (true) {
                if (!thread.isSuspended()) {
                    thread.suspend();
                }

                stepCommand.execute(debugger);

                EventSet eventSet = vm.eventQueue().remove();
                for (Event event : eventSet) {
                    if (event instanceof BreakpointEvent) {
                        System.out.println("Breakpoint hit at " + ((BreakpointEvent) event).location());
                        return; // Stop when a breakpoint is hit
                    }
                    if (event instanceof VMDisconnectEvent) {
                        System.out.println("Virtual Machine disconnected.");
                        return;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected: " + e.toString());
        } catch (AbsentInformationException e) {
            throw new RuntimeException(e);
        }
    }
}