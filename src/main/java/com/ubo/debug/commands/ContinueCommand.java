package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.LocatableEvent;
import com.ubo.debug.ScriptableDebugger;

public class ContinueCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) {
        VirtualMachine vm = debugger.getVm();

        try {
            vm.resume();
            EventSet eventSet;
            while ((eventSet = vm.eventQueue().remove()) != null) {
                for (Event event : eventSet) {
                    if (event instanceof BreakpointEvent) {
                        debugger.waitForUserInput((LocatableEvent) event);
                        return;
                    }
                    vm.resume();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected: " + e);
        } catch (AbsentInformationException e) {
            throw new RuntimeException(e);
        } catch (IncompatibleThreadStateException e) {
            throw new RuntimeException(e);
        }
    }
}
