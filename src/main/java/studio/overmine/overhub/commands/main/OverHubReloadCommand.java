package studio.overmine.overhub.commands.main;

import org.bukkit.command.CommandSender;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.SubCommand;

public class OverHubReloadCommand extends SubCommand {

    private final OverHub plugin;

    public OverHubReloadCommand(OverHub plugin) {
        super(
                "overhub.command.overhub.reload",
                "Reload the plugin configuration."
        );
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        plugin.onReload();
        String message = LanguageResource.OVERHUB_RELOAD_MESSAGE;
        if (message == null || message.trim().isEmpty()) {
            message = "&aOverHub has been reloaded.";
        }
        ChatUtil.sendMessage(sender, message);
    }
}
