package studio.overmine.overhub.commands.parkour.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import studio.overmine.overhub.models.parkour.ParkourSelection;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.SubCommand;

public class ParkourWandCommand extends SubCommand {
    public ParkourWandCommand() {
        super(
                "overhub.command.parkour.wand",
                "Get a wand to select parkour area",
                true
        );
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        ChatUtil.sendMessage(player,"&aYou have received the Parkour Wand.");
        player.getInventory().addItem(ParkourSelection.SELECTION_WAND);
    }
}
