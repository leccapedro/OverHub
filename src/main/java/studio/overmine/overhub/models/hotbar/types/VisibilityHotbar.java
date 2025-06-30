package studio.overmine.overhub.models.hotbar.types;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.user.VisibilityType;
import studio.overmine.overhub.controllers.HotbarController;
import studio.overmine.overhub.models.hotbar.Hotbar;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.cooldown.CooldownUtil;
import org.bukkit.entity.Player;

public class VisibilityHotbar extends Hotbar {

    private final OverHub plugin;
    private final HotbarController hotbarController;
    private final VisibilityType visibilityType;

    public VisibilityHotbar(
            String name,
            OverHub plugin,
            HotbarController hotbarController,
            VisibilityType visibilityType) {
        super(name, false);
        this.plugin = plugin;
        this.hotbarController = hotbarController;
        this.visibilityType = visibilityType;

    }

    @Override
    public void onAction(Player player) {
        if (CooldownUtil.hasCooldown(player, "visibility-hotbar")) {
            ChatUtil.sendMessage(player, "&cYou must wait " + CooldownUtil.getCooldownFormatted(player, "visibility-hotbar") + " before changing your visibility again.");
            return;
        }

        hotbarController.updateVisibilityHotbar(player, visibilityType);
        CooldownUtil.setCooldown(plugin, player, "visibility-hotbar", 5);
    }
}
