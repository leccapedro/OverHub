package studio.overmine.overhub.models.hotbar.types;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.HotbarController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.hotbar.Hotbar;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.VisibilityType;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.cooldown.CooldownUtil;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

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
            long cooldownMillis = CooldownUtil.getCooldown(player, "visibility-hotbar");
            long cooldownSeconds = cooldownMillis > 0L
                    ? TimeUnit.MILLISECONDS.toSeconds(cooldownMillis)
                    : 0L;

            ChatUtil.sendMessage(player, LanguageResource.VISIBILITY_MESSAGE_COOLDOWN
                    .replace("%cooldown%", String.valueOf(cooldownSeconds)));
            return;
        }

        User user = userController.getUser(player.getUniqueId());
        VisibilityType visibilityType = VisibilityType.getNext(user.getVisibilityType());

        hotbarController.updateVisibilityHotbar(user, player, visibilityType);
        CooldownUtil.setCooldown(plugin, player, "visibility-hotbar", 5);
    }
}
