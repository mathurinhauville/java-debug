package com.ubo.debug;

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ScriptableDebugger {

    private Class debugClass;
    private VirtualMachine vm;
    private final List<BreakpointRequest> breakpoints;
    private BreakpointManager breakpointManager;
    private CommandInterpreter interpreter;
    private Scanner scanner;

    public ScriptableDebugger() {
        breakpoints = new ArrayList<>();
        interpreter = new CommandInterpreter();
        scanner = new Scanner(System.in);
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
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableClassPrepareRequest(VirtualMachine vm) {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debugClass.getName());
        classPrepareRequest.enable();
    }

    public void startDebugger() throws VMDisconnectedException, InterruptedException, AbsentInformationException, IncompatibleThreadStateException {
        EventSet eventSet;
        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                System.out.println(event.toString());

                if (event instanceof ClassPrepareEvent) {
                    breakpointManager.loadBreakpointsFromFile();
                }

                if (event instanceof BreakpointEvent || event instanceof StepEvent) {
                    startCommandInterpreter();
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

    /**
     * Start the command interpreter for the user to interact with the debugger
     *
     * @throws AbsentInformationException       if the information about the source code is not available
     * @throws IncompatibleThreadStateException if the thread is not in the expected state
     */
    public void startCommandInterpreter() throws AbsentInformationException, IncompatibleThreadStateException {
        while (true) {
            System.out.print("debugger> ");
            String command = scanner.nextLine().trim();
            interpreter.executeCommand(command, this);
        }
    }

    public BreakpointManager getBreakpointManager() {
        return breakpointManager;
    }

    public VirtualMachine getVm() {
        return vm;
    }

}