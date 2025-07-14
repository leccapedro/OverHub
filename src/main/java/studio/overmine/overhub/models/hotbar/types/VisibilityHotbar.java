package studio.overmine.overhub.models.hotbar.types;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.HotbarController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.hotbar.Hotbar;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.VisibilityType;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.cooldown.CooldownUtil;
import org.bukkit.entity.Player;

public class VisibilityHotbar extends Hotbar {

    private final OverHub plugin;
    private final UserController userController;
    private final HotbarController hotbarController;

    public VisibilityHotbar(
            String name,
            OverHub plugin,
            UserController userController,
            HotbarController hotbarController) {
        super(name, false);
        this.plugin = plugin;
        this.userController = userController;
        this.hotbarController = hotbarController;
    }

    @Override
    public void onAction(Player player) {
        if (CooldownUtil.hasCooldown(player, "visibility-hotbar")) {
            ChatUtil.sendMessage(player, "&cYou must wait " + CooldownUtil.getCooldownFormatted(player, "visibility-hotbar") + " before changing your visibility again.");
            return;
        }

        User user = userController.getUser(player.getUniqueId());
        VisibilityType visibilityType = VisibilityType.getNext(user.getVisibilityType());

        hotbarController.updateVisibilityHotbar(user, player, visibilityType);
        CooldownUtil.setCooldown(plugin, player, "visibility-hotbar", 5);
    }
}
