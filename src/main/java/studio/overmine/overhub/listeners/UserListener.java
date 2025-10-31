package studio.overmine.overhub.listeners;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.utilities.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserListener implements Listener {

    private final OverHub plugin;
    private final UserController userController;

    public UserListener(OverHub plugin) {
        this.plugin = plugin;
        this.userController = plugin.getUserController();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        User user = userController.createUser(event.getUniqueId(), event.getName());
        userController.loadUser(user);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        User user = userController.getUser(event.getPlayer().getUniqueId());

        if (user == null) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatUtil.translate("&c[OverHub] Ingresa nuevamente al servidor."));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = userController.getUser(player.getUniqueId());
        if (user == null) return;

        user.executeCurrentVisibility();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = userController.getUser(player.getUniqueId());

        if (user != null) {
            userController.saveUser(user);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> userController.destroyUser(user));
        }
    }
}