package studio.overmine.overhub.utilities.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class SubCommand {

    private final String description;
    private final Set<String> parameters;
    private final String permission;
    private final boolean playerOnly;

    public SubCommand(Set<String> parameters, String permission, String description, boolean playerOnly) {
        this.parameters = parameters;
        this.permission = permission;
        this.description = description;
        this.playerOnly = playerOnly;
    }

    public SubCommand(Set<String> parameters, String permission, String description) {
        this(parameters, permission, description, false);
    }

    public SubCommand(Set<String> parameter, String description, boolean playerOnly) {
        this(parameter, "", description, playerOnly);
    }

    public SubCommand(Set<String> parameter, String description) {
        this(parameter, "", description, false);
    }

    public SubCommand(String permission, String description, boolean playerOnly) {
        this(new HashSet<>(), permission, description, playerOnly);
    }

    public SubCommand(String permission, String description) {
        this(new HashSet<>(), permission, description, false);
    }

    public SubCommand(String description, boolean playerOnly) {
        this(new HashSet<>(), "", description, playerOnly);
    }

    public SubCommand(String description) {
        this(new HashSet<>(), "", description, false);
    }

    public String getParametersFormatted() {
        return parameters.isEmpty() ? "" : " " + String.join(" ", parameters);
    }

    public boolean hasPermission(CommandSender sender) {
        return permission.isEmpty() || sender.hasPermission(permission);
    }

    public abstract void execute(CommandSender sender, String label, String[] args);
}
