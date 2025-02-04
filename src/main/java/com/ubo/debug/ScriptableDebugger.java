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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Scanner;

public class ScriptableDebugger {

    private Class debugClass;
    private VirtualMachine vm;
    private boolean autoStepping = false;

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

    public void setBreakPoint(String className, int lineNumber) throws AbsentInformationException {
        for (ReferenceType targetClass : vm.allClasses()) {
            if (targetClass.name().equals(className)) {
                Location location = targetClass.locationsOfLine(lineNumber).get(0);
                BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                bpReq.enable();
            }
        }
    }

    public void enableStepRequest(LocatableEvent event) {
        // Supprime les StepRequest existants pour ce thread
        vm.eventRequestManager().deleteEventRequests(vm.eventRequestManager().stepRequests());

        StepRequest stepRequest = vm.eventRequestManager()
                .createStepRequest(event.thread(), StepRequest.STEP_MIN, StepRequest.STEP_OVER);
        stepRequest.enable();
    }

    private void waitForUserInput(LocatableEvent event) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Debugger paused. Type 'step' to continue stepping, or press Enter to auto-step until the end.");
        try {
            String input = reader.readLine();
            if ("step".equalsIgnoreCase(input)) {
                autoStepping = false;
                enableStepRequest(event);
            } else {
                autoStepping = true;
                enableStepRequest(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void startDebugger() throws VMDisconnectedException, InterruptedException, AbsentInformationException {
        EventSet eventSet;
        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                System.out.println(event.toString());

                if (event instanceof ClassPrepareEvent) {
                    setBreakPoint(debugClass.getName(), 6);
                }

                if (event instanceof BreakpointEvent || event instanceof StepEvent) {
                    if (!autoStepping) {
                        waitForUserInput((LocatableEvent) event);
                    } else {
                        enableStepRequest((LocatableEvent) event);
                    }
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

    public ScriptableDebugger(VirtualMachine vm) {
        this.vm = vm;
    }

    public void step() throws AbsentInformationException {
        ThreadReference thread = vm.allThreads().get(0);
        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(
                thread, StepRequest.STEP_MIN, StepRequest.STEP_INTO);
        stepRequest.enable();
        vm.resume();
    }

    public void stepOver() throws AbsentInformationException {
        ThreadReference thread = vm.allThreads().get(0);
        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(
                thread, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
        stepRequest.enable();
        vm.resume();
    }

    public void continueExecution() {
        vm.resume();
    }

    public void printCurrentFrame() throws IncompatibleThreadStateException {
        ThreadReference thread = vm.allThreads().get(0);
        System.out.println(thread.frame(0));
    }

    public void printTemporaries() throws IncompatibleThreadStateException, AbsentInformationException {
        ThreadReference thread = vm.allThreads().get(0);
        StackFrame frame = thread.frame(0);
        for (LocalVariable var : frame.visibleVariables()) {
            System.out.println(var.name() + " -> " + frame.getValue(var));
        }
    }

    public void printStack() throws IncompatibleThreadStateException {
        ThreadReference thread = vm.allThreads().get(0);
        for (StackFrame frame : thread.frames()) {
            System.out.println(frame);
        }
    }

    public void printReceiver() throws IncompatibleThreadStateException {
        ThreadReference thread = vm.allThreads().get(0);
        System.out.println(thread.frame(0).thisObject());
    }

    public void printSender() {
        System.out.println("Not directly available in JDI");
    }

    public void printReceiverVariables() throws IncompatibleThreadStateException {
        ObjectReference receiver = vm.allThreads().get(0).frame(0).thisObject();
        for (Field field : receiver.referenceType().allFields()) {
            System.out.println(field.name() + " -> " + receiver.getValue(field));
        }
    }

    public void printMethod() throws IncompatibleThreadStateException {
        System.out.println(vm.allThreads().get(0).frame(0).location().method());
    }

    public void printArguments() throws IncompatibleThreadStateException, AbsentInformationException {
        StackFrame frame = vm.allThreads().get(0).frame(0);
        for (LocalVariable var : frame.visibleVariables()) {
            if (var.isArgument()) {
                System.out.println(var.name() + " -> " + frame.getValue(var));
            }
        }
    }

    public void printVariable(String varName) throws IncompatibleThreadStateException, AbsentInformationException {
        StackFrame frame = vm.allThreads().get(0).frame(0);
        LocalVariable var = frame.visibleVariableByName(varName);
        System.out.println(var.name() + " -> " + frame.getValue(var));
    }

    public void setBreakpoint() throws AbsentInformationException {
        System.out.print("Enter class name: ");
        Scanner scanner = new Scanner(System.in);
        String className = scanner.nextLine().trim();
        System.out.print("Enter line number: ");
        int lineNumber = scanner.nextInt();
        for (ReferenceType targetClass : vm.allClasses()) {
            if (targetClass.name().equals(className)) {
                Location location = targetClass.locationsOfLine(lineNumber).get(0);
                BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                bpReq.enable();
            }
        }
    }

    public void listBreakpoints() {
        System.out.println("Breakpoints are not directly retrievable in JDI");
    }

    public void setOneTimeBreakpoint() throws AbsentInformationException {
        setBreakpoint();
        // Logique supplémentaire pour supprimer après une activation
    }

    public void setConditionalBreakpoint() throws AbsentInformationException {
        setBreakpoint();
        System.out.print("Enter hit count before activation: ");
        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
        // Logique supplémentaire pour l'activer après un certain nombre d'atteintes
    }

    public void setMethodCallBreakpoint() {
        System.out.print("Enter method name: ");
        Scanner scanner = new Scanner(System.in);
        String methodName = scanner.nextLine().trim();
        System.out.println("Not natively supported by JDI");
    }
}