package studio.overmine.overhub.models.hotbar.types;

import com.cryptomorin.xseries.XSound;
import studio.overmine.overhub.models.hotbar.Hotbar;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EnderButtHotbar extends Hotbar {

    public EnderButtHotbar(String name) {
        super(name, true);
    }

    @Override
    public void onAction(Player player) {
        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
        player.setVelocity(player.getLocation().getDirection().normalize().setY(2.0));
        player.setVelocity(player.getLocation().getDirection().normalize().multiply(2F));
        player.updateInventory();
    }
}
