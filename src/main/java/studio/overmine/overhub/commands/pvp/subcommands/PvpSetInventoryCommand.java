package studio.overmine.overhub.commands.pvp.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.SubCommand;

public class PvpSetInventoryCommand extends SubCommand {

    private final OverHub plugin;

    public PvpSetInventoryCommand(OverHub plugin) {
        super("overhub.command.pvp.setinventory", "Save the current inventory as the PvP layout", true);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendMessage(sender, "&cYou must be a player to use this command.");
            return;
        }

        Player player = (Player) sender;

        if (!ConfigResource.PVP_MODE_ENABLED) {
            ChatUtil.sendMessage(player, "&cPvP mode is currently disabled.");
            return;
        }

        CombatController combatController = plugin.getCombatController();
        CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
        if (combatPlayer != null && combatPlayer.isPvP()) {
            ChatUtil.sendMessage(player, LanguageResource.COMBAT_PVP_LAYOUT_DENIED);
            return;
        }

        UserController userController = plugin.getUserController();
        User user = userController.getUser(player.getUniqueId());
        if (user == null) {
            ChatUtil.sendMessage(player, "&cUser data is not loaded. Try rejoining.");
            return;
        }

        ItemStack[] storageContents = player.getInventory().getStorageContents();
        user.setPvpHotbar(storageContents);
        userController.saveUser(user);

        ChatUtil.sendMessage(player, LanguageResource.COMBAT_PVP_LAYOUT_SAVED);
    }
}
