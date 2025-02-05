package com.ubo.debug;

import com.sun.jdi.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;

import java.io.*;
import java.util.*;

public class BreakpointManager {
    private final VirtualMachine vm;
    private final List<BreakpointRequest> breakpoints = new ArrayList<>();
    private final Set<String> setBreakpoints = new HashSet<>();
    private static final String BREAKPOINTS_FILE = "breakpoints.txt";

    public BreakpointManager(VirtualMachine vm) {
        this.vm = vm;
    }

    public List<BreakpointRequest> getBreakpoints() {
        return breakpoints;
    }

    public void setBreakpoint(String className, int lineNumber) throws AbsentInformationException {
        String key = className + ":" + lineNumber;
        if (setBreakpoints.contains(key)) return;

        for (ReferenceType targetClass : vm.allClasses()) {
            if (targetClass.name().equals(className)) {
                List<Location> locations = targetClass.locationsOfLine(lineNumber);
                if (!locations.isEmpty()) {
                    BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(locations.getFirst());
                    bpReq.enable();
                    breakpoints.add(bpReq);
                    setBreakpoints.add(key);
                    saveBreakpointToFile(className, lineNumber);
                    System.out.println("Breakpoint set at " + className + ":" + lineNumber);
                }
                return;
            }
        }

        // Si la classe n'est pas encore chargée, on ajoute un ClassPrepareRequest
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(className);
        classPrepareRequest.enable();
    }

    private void saveBreakpointToFile(String className, int lineNumber) {
        try (FileWriter writer = new FileWriter(BREAKPOINTS_FILE, true)) {
            writer.write(className + ":" + lineNumber + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBreakpointsFromFile() {
        File file = new File(BREAKPOINTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    setBreakpoint(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (IOException | AbsentInformationException e) {
            e.printStackTrace();
        }
    }
}