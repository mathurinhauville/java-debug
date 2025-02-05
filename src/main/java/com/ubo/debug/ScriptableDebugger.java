package com.ubo.debug;

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.StepRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class ScriptableDebugger {

    private Class debugClass;
    private VirtualMachine vm;
    private final List<BreakpointRequest> breakpoints;
    private BreakpointManager breakpointManager;

    public ScriptableDebugger() {
        breakpoints = new ArrayList<>();
    }

    public void reapplyBreakpoints() {
        for (BreakpointRequest bpReq : breakpoints) {
            bpReq.enable();
        }
    }

    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        return launchingConnector.launch(arguments);
    }

    public void attachTo(Class debuggeeClass) {
        this.debugClass = debuggeeClass;
        try {
            vm = connectAndLaunchVM();
            enableClassPrepareRequest(vm);
            breakpointManager = new BreakpointManager(vm);
            startDebugger();
        } catch (IOException | IllegalConnectorArgumentsException | VMStartException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableClassPrepareRequest(VirtualMachine vm) {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debugClass.getName());
        classPrepareRequest.enable();
    }

    public BreakpointManager getBreakpointManager() {
        return breakpointManager;
    }

    public void enableStepRequest(LocatableEvent event) {
        // Supprime les StepRequest existants pour ce thread
        vm.eventRequestManager().deleteEventRequests(vm.eventRequestManager().stepRequests());

        StepRequest stepRequest = vm.eventRequestManager()
                .createStepRequest(event.thread(), StepRequest.STEP_MIN, StepRequest.STEP_OVER);
        stepRequest.enable();
    }

    private void waitForUserInput(LocatableEvent event) throws AbsentInformationException, IncompatibleThreadStateException {
        CommandInterpreter interpreter = new CommandInterpreter();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("debugger> ");
            String command = scanner.nextLine().trim();
            if (command.equals("exit")) break;
            interpreter.executeCommand(command, this);
        }
    }

    public void startDebugger() throws VMDisconnectedException, InterruptedException, AbsentInformationException, IncompatibleThreadStateException {
        breakpointManager.loadBreakpointsFromFile();
        reapplyBreakpoints();
        EventSet eventSet;
        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                System.out.println(event.toString());

                if (event instanceof ClassPrepareEvent) {
                    breakpointManager.setBreakpoint(debugClass.getName(), 6);
                }

                if (event instanceof BreakpointEvent || event instanceof StepEvent) {
                    waitForUserInput((LocatableEvent) event);
                }

                if (event instanceof VMDisconnectEvent) {
                    System.out.println("End of program");
                    InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
                    OutputStreamWriter writer = new OutputStreamWriter(System.out);
                    try {
                        reader.transferTo(writer);
                        writer.flush();
                    } catch (IOException e) {
                        System.out.println("Target VM input stream reading error.");
                    }
                }
                vm.resume();
            }
        }
    }

    public VirtualMachine getVm() {
        return vm;
    }

    public void startCommandInterpreter() throws AbsentInformationException, IncompatibleThreadStateException {
        CommandInterpreter interpreter = new CommandInterpreter();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("debugger> ");
            String command = scanner.nextLine().trim();
            if (command.equals("exit")) break;
            interpreter.executeCommand(command, this);
        }
    }
}