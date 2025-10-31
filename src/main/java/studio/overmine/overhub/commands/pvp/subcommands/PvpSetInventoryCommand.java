package studio.overmine.overhub.commands.pvp.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.controllers.HotbarController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.SubCommand;
import studio.overmine.overhub.utilities.BukkitUtil;

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

        HotbarController hotbarController = plugin.getHotbarController();
        PlayerInventory inv = player.getInventory();
        ItemStack[] storageContents = hotbarController.createStorageSnapshot(inv);
        hotbarController.saveGlobalPvpLayout(storageContents);
        ItemStack offhand = (BukkitUtil.SERVER_VERSION >= 9) ? inv.getItemInOffHand() : null;
        hotbarController.saveGlobalPvpEquipment(inv.getArmorContents(), offhand);
        user.setSavedPvpLayout(null);
        userController.saveUser(user);

        ChatUtil.sendMessage(player, LanguageResource.COMBAT_PVP_LAYOUT_SAVED);
    }
}