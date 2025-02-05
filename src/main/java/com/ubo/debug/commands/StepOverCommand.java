package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;
import com.ubo.debug.ScriptableDebugger;

/**
 * Passe à la ligne suivante dans la méthode courante.
 */
public class StepOverCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws AbsentInformationException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();

        vm.eventRequestManager().deleteEventRequests(vm.eventRequestManager().stepRequests());
        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(
                thread, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
        stepRequest.enable();
        vm.resume();
    }
}
