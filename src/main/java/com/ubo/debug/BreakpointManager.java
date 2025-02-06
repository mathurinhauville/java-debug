package com.ubo.debug;

import com.sun.jdi.*;
import com.sun.jdi.request.BreakpointRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère les points d'arrêt
 */
public class BreakpointManager {
    private static final String BREAKPOINTS_FILE = "breakpoints.txt";

    private final VirtualMachine vm;
    private final List<BreakpointRequest> breakpoints;

    public BreakpointManager(VirtualMachine vm) {
        this.vm = vm;
        this.breakpoints = new ArrayList<>();
    }

    /**
     * Vérifie si le nom de la classe est valide
     *
     * @param className Nom de la classe à vérifier
     * @return true si le nom de la classe est valide, false sinon
     */
    private boolean isValidClassName(String className) {
        for (ReferenceType targetClass : vm.allClasses()) {
            if (targetClass.name().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si un point d'arrêt est défini à l'emplacement donné
     *
     * @param className  Le nom de la classe
     * @param lineNumber Le numéro de ligne
     * @return true si un point d'arrêt est défini à l'emplacement donné, false sinon
     */
    private boolean isBreakpointSet(String className, int lineNumber) {
        for (BreakpointRequest bpReq : breakpoints) {
            Location location = bpReq.location();
            if (location.declaringType().name().equals(className) && location.lineNumber() == lineNumber) {
                return true;
            }
        }
        return false;
    }

    /**
     * Supprime un point d'arrêt
     *
     * @param className  Le nom de la classe
     * @param lineNumber Le numéro de ligne
     */
    public void setBreakpoint(String className, int lineNumber) throws AbsentInformationException {
        // Vérifie si le nom de la classe est valide
        if (!isValidClassName(className)) {
            System.out.println("Invalid class name: " + className);
            return;
        }

        // Vérifie si un point d'arrêt est déjà défini à l'emplacement donné
        if (isBreakpointSet(className, lineNumber)) {
            System.out.println("Breakpoint already set at " + className + ":" + lineNumber);
            return;
        }

        // Crée un point d'arrêt à l'emplacement donné
        for (ReferenceType targetClass : vm.allClasses()) {
            if (targetClass.name().equals(className)) {
                Location location = targetClass.locationsOfLine(lineNumber).getFirst();
                BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                bpReq.enable();
                breakpoints.add(bpReq);
            }
        }
    }

    /**
     * Liste les points d'arrêt
     */
    public void listBreakpoints() {
        for (BreakpointRequest bpReq : breakpoints) {
            Location location = bpReq.location();
            System.out.println("Breakpoint at " + location.declaringType().name() + ":" + location.lineNumber());
        }
    }

    /**
     * Sauvegarde un point d'arrêt dans un fichier
     *
     * @param className  Nom de la classe du point d'arrêt
     * @param lineNumber Numéro de ligne du point d'arrêt
     */
    public void saveBreakpointToFile(String className, int lineNumber) {
        String breakpoint = className + ":" + lineNumber;

        try (BufferedReader reader = new BufferedReader(new FileReader(BREAKPOINTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(breakpoint)) {
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(BREAKPOINTS_FILE, true)) {
            writer.write(breakpoint + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Charge les points d'arrêt depuis un fichier
     */
    void loadBreakpointsFromFile() {
        File file = new File(BREAKPOINTS_FILE);

        // Vérifie si le fichier de points d'arrêt existe
        if (!file.exists()) {
            System.out.println(BREAKPOINTS_FILE + " file not found.");
            System.out.println("Create a breakpoints.txt file inside target/classes directory.");
            System.out.println("Add a breakpoint in the format: ClassName:LineNumber");
            System.out.println("Example: com.ubo.debug.JDISimpleDebuggee:8");
            return;
        }

        System.out.println("Load breakpoints from " + BREAKPOINTS_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");

                // Vérifie si le point d'arrêt est valide
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

                // Définit le point d'arrêt
                setBreakpoint(className, lineNumber);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AbsentInformationException e) {
            throw new RuntimeException(e);
        }
    }

}