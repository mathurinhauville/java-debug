package com.ubo.debug.commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;
import com.ubo.debug.ScriptableDebugger;

/**
 * Execute la prochaine instruction. S’il s’agit d’un appel de méthode,
 * l’exécution entre dans cette dernière.
 */
public class StepCommand implements DebuggerCommand {

    @Override
    public void execute(ScriptableDebugger debugger) throws AbsentInformationException {
        VirtualMachine vm = debugger.getVm();
        ThreadReference thread = vm.allThreads().getFirst();

        if (!thread.isSuspended()) {
            thread.suspend();
        }

        vm.eventRequestManager().deleteEventRequests(vm.eventRequestManager().stepRequests());
        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(
                thread, StepRequest.STEP_MIN, StepRequest.STEP_INTO);
        stepRequest.enable();

        // Incrémente le PC
        debugger.setPC(debugger.getPC() + 1);
        vm.resume();
    }
}
