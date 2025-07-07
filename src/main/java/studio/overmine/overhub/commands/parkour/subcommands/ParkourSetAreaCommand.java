package studio.overmine.overhub.commands.parkour.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import studio.overmine.overhub.controllers.ParkourController;
import studio.overmine.overhub.utilities.command.SubCommand;

public class ParkourSetAreaCommand extends SubCommand {
    private final ParkourController parkourController;

    public ParkourSetAreaCommand(ParkourController parkourController) {
        super(
                "overhub.command.parkour.setarea",
                "Define parkour area",
                true
        );
        this.parkourController = parkourController;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        parkourController.setCuboid(player);
    }
}
