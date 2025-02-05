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
import java.util.*;

public class ScriptableDebugger {

    private Class<?> debugClass;
    private VirtualMachine vm;
    private final List<BreakpointRequest> breakpoints;
    private final Set<String> setBreakpoints;
    private boolean breakpointsSet = false;
    private BreakpointManager breakpointManager;

    public ScriptableDebugger() {
        breakpoints = new ArrayList<>();
        setBreakpoints = new HashSet<>();
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

    public void attachTo(Class<?> debuggeeClass) {
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

    public void setBreakpoint(String className, int lineNumber) {
        String breakpointKey = className + ":" + lineNumber;
        if (setBreakpoints.contains(breakpointKey)) {
            return;
        }

        boolean classFound = false;
        for (ReferenceType targetClass : vm.allClasses()) {
            if (targetClass.name().equals(className)) {
                classFound = true;
                try {
                    List<Location> locations = targetClass.locationsOfLine(lineNumber);
                    if (locations.isEmpty()) {
                        System.out.println("No locations found for line: " + lineNumber);
                        return;
                    }
                    Location location = locations.get(0);
                    BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                    bpReq.enable();
                    breakpoints.add(bpReq);
                    setBreakpoints.add(breakpointKey);
                    System.out.println("Breakpoint set at " + targetClass.sourceName() + ":" + lineNumber);
                    saveBreakpointToFile(className, lineNumber);
                } catch (AbsentInformationException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        if (!classFound) {
            ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
            classPrepareRequest.addClassFilter(className);
            classPrepareRequest.enable();
        }
    }

    private void saveBreakpointToFile(String className, int lineNumber) {
        String breakpoint = className + ":" + lineNumber;
        try (BufferedReader reader = new BufferedReader(new FileReader("breakpoints.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(breakpoint)) {
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter("breakpoints.txt", true)) {
            writer.write(breakpoint + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBreakpointsFromFile() {
        File file = new File("breakpoints.txt");
        if (!file.exists()) {
            System.out.println("No breakpoints file found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length != 2) {
                    System.out.println("Invalid breakpoint format: " + line);
                    continue;
                }
                String className = parts[0];
                int lineNumber;
                try {
                    lineNumber = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid line number: " + parts[1]);
                    continue;
                }
                setBreakpoint(className, lineNumber);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDebugger() throws VMDisconnectedException, InterruptedException, AbsentInformationException, IncompatibleThreadStateException {
        loadBreakpointsFromFile();
        System.out.println(breakpoints.toString());
        reapplyBreakpoints();
        EventSet eventSet;
        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                System.out.println(event.toString());

                if (event instanceof ClassPrepareEvent) {
                    ClassPrepareEvent classPrepareEvent = (ClassPrepareEvent) event;
                    if (classPrepareEvent.referenceType().name().equals(debugClass.getName()) && !breakpointsSet) {
                        setBreakpoint(debugClass.getName(), 6);
                        breakpointsSet = true;
                    }
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

    public void waitForUserInput(LocatableEvent event) throws AbsentInformationException, IncompatibleThreadStateException {
        CommandInterpreter interpreter = new CommandInterpreter();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("debugger> ");
            String command = scanner.nextLine().trim();
            if (command.equals("continue")) {
                vm.resume();
                break;
            }
            interpreter.executeCommand(command, this);
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