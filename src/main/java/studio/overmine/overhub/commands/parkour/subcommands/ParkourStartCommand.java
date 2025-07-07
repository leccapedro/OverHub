package studio.overmine.overhub.commands.parkour.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import studio.overmine.overhub.controllers.ParkourController;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.SubCommand;

public class ParkourStartCommand extends SubCommand {
    private final ParkourController parkourController;

    public ParkourStartCommand(ParkourController parkourController) {
        super(
                "overhub.command.parkour.start",
                "Start parkour to a player",
                false
        );
        this.parkourController = parkourController;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            ChatUtil.sendMessage(sender, "&cUsage: /parkour start <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            ChatUtil.sendMessage(sender, "&cPlayer not found.");

        } else if (!parkourController.getParkours().containsKey(target.getUniqueId())) {
            parkourController.startParkour(target);
            ChatUtil.sendMessage(sender, "&aParkour started to " + target.getName());

        } else {
            ChatUtil.sendMessage(sender, "&cParkour already started.");
        }
    }
}
