package studio.overmine.overhub.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import studio.overmine.overhub.models.resources.types.CombatSwordResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;

import java.util.function.Consumer;

public class CombatTask extends BukkitRunnable {
    private final Player player;
    private final Consumer<Player> equipAction;
    private int countdown;

    public CombatTask(Player player, Consumer<Player> equipAction) {
        this.player = player;
        this.equipAction = equipAction;
        this.countdown = CombatSwordResource.EQUIP_DELAY;
    }

    @Override
    public void run() {
        if (countdown > 0) {
            ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_TASK_COUNTDOWN.replace("%countdown%", String.valueOf(countdown)));
            countdown--;
        } else {
            equipAction.accept(player);
            ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_TASK_START);
            cancel();
        }
    }
}
