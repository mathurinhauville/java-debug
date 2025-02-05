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
        for (ReferenceType targetClass : vm.allClasses()) {
            if (targetClass.name().equals(className)) {
                Location location = targetClass.locationsOfLine(lineNumber).get(0);
                BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                bpReq.enable();
                breakpoints.add(bpReq);
            }
        }
    }

    public void listBreakpoints() {
        for (BreakpointRequest bpReq : breakpoints) {
            Location location = bpReq.location();
            System.out.println("Breakpoint at " + location.declaringType().name() + ":" + location.lineNumber());
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


    void loadBreakpointsFromFile() {
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
        } catch (AbsentInformationException e) {
            throw new RuntimeException(e);
        }
    }

}