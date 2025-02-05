package com.ubo.debug.commands;

import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.VirtualMachine;
import com.ubo.debug.ScriptableDebugger;

public class ReceiverVariablesCommand implements DebuggerCommand {
    public void execute(ScriptableDebugger debugger) throws IncompatibleThreadStateException {
        VirtualMachine vm = debugger.getVm();

        ObjectReference receiver = vm.allThreads().getFirst().frame(0).thisObject();
        for (Field field : receiver.referenceType().allFields()) {
            System.out.println(field.name() + " -> " + receiver.getValue(field));
        }
    }
}