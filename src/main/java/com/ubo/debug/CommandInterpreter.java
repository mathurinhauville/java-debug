package com.ubo.debug;

import com.sun.jdi.*;
import com.ubo.debug.commands.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Interprète les commandes saisies par l'utilisateur.
 */
public class CommandInterpreter {
    private final Map<String, DebuggerCommand> commands = new HashMap<>();

    public CommandInterpreter() {
        commands.put("step", new StepCommand());
        commands.put("step-over", new StepOverCommand());
        commands.put("continue", new ContinueCommand());
        commands.put("frame", new FrameCommand());
        commands.put("temporaries", new TemporariesCommand());
        commands.put("stack", new StackCommand());
        commands.put("receiver", new ReceiverCommand());
        commands.put("sender", new SenderCommand());
        commands.put("receiver-variables", new ReceiverVariablesCommand());
        commands.put("method", new MethodCommand());
        commands.put("arguments", new ArgumentsCommand());
        commands.put("print-var", new PrintVarCommand());
        commands.put("break", new BreakCommand());
        commands.put("breakpoints", new BreakpointsCommand());
        commands.put("break-once", new BreakOnceCommand());
        commands.put("break-on-count", new BreakOnCountCommand());
        commands.put("break-before-method-call", new BreakBeforeMethodCallCommand());
        commands.put("step-back-one", new StepBackOneCommand());
        commands.put("step-back", new StepBackCommand());
        commands.put("pc", new PcCommand());
    }

    /**
     * Exécute la commande spécifiée.
     * Si la commande est inconnue, affiche un message d'erreur.
     *
     * @param command  la commande à exécuter
     * @param debugger le débogueur
     * @throws AbsentInformationException       si une information est absente
     * @throws IncompatibleThreadStateException si l'état du thread est incompatible
     */
    public void executeCommand(String command, ScriptableDebugger debugger) throws AbsentInformationException, IncompatibleThreadStateException {
        DebuggerCommand cmd = commands.get(command);
        if (cmd != null) {
            cmd.execute(debugger);
        } else {
            System.out.println("Commande inconnue : " + command);
        }
    }

}
