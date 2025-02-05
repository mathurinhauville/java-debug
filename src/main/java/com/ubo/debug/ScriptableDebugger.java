package com.ubo.debug;

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.ClassPrepareRequest;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

public class ScriptableDebugger {

    private Class debugClass;
    private VirtualMachine vm;
    private BreakpointManager breakpointManager;

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

                // Si l'événement est un ClassPrepareEvent, on charge les points d'arrêt depuis le fichier
                if (event instanceof ClassPrepareEvent) {
                    breakpointManager.loadBreakpointsFromFile();
                }

                // Si l'événement est un BreakpointEvent ou un StepEvent, on lance l'interpréteur de commandes
                if (event instanceof BreakpointEvent || event instanceof StepEvent) {
                    startCommandInterpreter();
                }

                // Si l'événement est un VMDisconnectEvent, on affiche la sortie de la VM cible
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
     * Lance l'interpréteur de commandes.
     * L'interpréteur lit les commandes saisies par l'utilisateur et les exécute.
     * L'interpréteur s'arrête lorsque l'utilisateur saisit la commande "exit".
     */
    public void startCommandInterpreter() throws AbsentInformationException, IncompatibleThreadStateException {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            CommandInterpreter interpreter = new CommandInterpreter();

            System.out.print("debugger> ");
            String command = scanner.nextLine().trim();
            if (command.equals("exit")) break;
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