package studio.overmine.overhub.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.ParkourController;
import studio.overmine.overhub.models.parkour.ParkourPlayer;
import studio.overmine.overhub.models.parkour.ParkourSelection;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;

public class ParkourListener {
    private final OverHub plugin;
    private final ParkourController parkourController;

    public ParkourListener(OverHub plugin) {
        this.plugin = plugin;
        this.parkourController = plugin.getParkourController();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ParkourPlayer parkour = parkourController.getParkours().get(player.getUniqueId());

        if (parkour != null) parkourController.stopParkour(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ParkourPlayer parkour = parkourController.getParkours().get(player.getUniqueId());
        if (parkour == null) return;

        Location nextBlock = parkour.getNextTargetBlock();
        Location playerLocation = player.getLocation();

        double playerY = playerLocation.getY();
        double lowestBlockY = parkour.getLowestBlockHeight();

        if (playerY < lowestBlockY - 1) {
            ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_FALL.replace("%score%", String.valueOf(parkour.getScore())));

            Bukkit.getScheduler().runTask(plugin, () -> player.teleport(parkour.getPreviousLocation()));

            if (parkour.isNewHighScore(plugin.getUserController().getUser(player.getUniqueId()))) {
                ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_NEW_HS.replace("%score%", String.valueOf(parkour.getScore())));
                plugin.getUserController().getUser(player.getUniqueId()).setParkourScore(parkour.getScore());
            }

            plugin.getParkourController().stopParkour(player);

            return;
        }

        if (Math.abs(playerY - nextBlock.getY()) < 1.5) {
            double blockMinX = nextBlock.getX() - 0.2;
            double blockMaxX = nextBlock.getX() + 1.2;
            double blockMinZ = nextBlock.getZ() - 0.2;
            double blockMaxZ = nextBlock.getZ() + 1.2;

            double playerX = playerLocation.getX();
            double playerZ = playerLocation.getZ();

            if (playerX >= blockMinX && playerX <= blockMaxX &&
                    playerZ >= blockMinZ && playerZ <= blockMaxZ) {
                parkour.advance(player);
                if (parkour.getScore() % ConfigResource.PARKOUR_STREAK_STREAK_INTERVAL == 0) {
                    ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_STREAK.replace("%score%", String.valueOf(parkour.getScore())));
                    if (!ConfigResource.PARKOUR_STREAK_COMMANDS.isEmpty())
                        ConfigResource.PARKOUR_STREAK_COMMANDS.forEach(
                                string -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), ChatUtil.placeholder(player, string)));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWandInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK))
            return;

        if (event.getItem() != null && event.getItem().isSimilar(ParkourSelection.SELECTION_WAND) && event.getClickedBlock() != null) {
            Block clicked = event.getClickedBlock();
            Player player = event.getPlayer();
            int location = 0;

            ParkourSelection selection = ParkourSelection.createOrGetSelection(plugin, player);

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                selection.setPoint2(clicked.getLocation());
                location = 2;
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                selection.setPoint1(clicked.getLocation());
                location = 1;
            }

            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);

            String message = "&a" + (location == 1 ? "First" : "Second") +
                    " location " + "&7(&f" +
                    clicked.getX() + "&7,&f" +
                    clicked.getY() + "&7,&f" +
                    clicked.getZ() + "&7)" + "&b has been set!";

            if (selection.isFullObject()) {
                message += "&7 (&f" + selection.getCuboid().volume() + " &eblocks" +
                        "&7)";
            }
            ChatUtil.sendMessage(player, message);
        }
    }
}
