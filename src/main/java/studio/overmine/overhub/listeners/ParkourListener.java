package studio.overmine.overhub.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.ParkourController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.parkour.ParkourPlayer;
import studio.overmine.overhub.models.parkour.ParkourSelection;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;

public class ParkourListener implements Listener, PacketListener {
    private final OverHub plugin;
    private final ParkourController parkourController;
    private final UserController userController;

    public ParkourListener(OverHub plugin) {
        this.plugin = plugin;
        this.parkourController = plugin.getParkourController();
        this.userController = plugin.getUserController();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        parkourController.stopParkour(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ParkourPlayer parkour = parkourController.getParkour(player);
        if (parkour == null) return;

        Location nextBlock = parkour.getNextTargetBlock();
        if (nextBlock == null) return;

        Location playerLoc = player.getLocation();
        double playerY = playerLoc.getY();
        double lowestBlockY = parkour.getLowestBlockHeight();

        if (playerY < lowestBlockY - 1) {
            if (parkour.isNewHighScore(player.getUniqueId())) {
                ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_NEW_HS.replace("%score%", String.valueOf(parkour.getScore())));
                userController.getUser(player.getUniqueId()).setParkourHS(parkour.getScore());
            }
            else ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_FALL.replace("%score%", String.valueOf(parkour.getScore())));
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(parkour.getPreviousLocation());
                }
            }.runTask(plugin);
            parkourController.stopParkour(player);
            return;
        }

        double blockMinX = nextBlock.getX() - 0.3;
        double blockMaxX = nextBlock.getX() + 1.3;
        double blockMinZ = nextBlock.getZ() - 0.3;
        double blockMaxZ = nextBlock.getZ() + 1.3;

        if (Math.abs(playerY - nextBlock.getY()) < 1.5 &&
                playerLoc.getX() >= blockMinX && playerLoc.getX() <= blockMaxX &&
                playerLoc.getZ() >= blockMinZ && playerLoc.getZ() <= blockMaxZ) {
            parkour.advance();
        }
    }

//    @Override
//    public void onPacketReceive(PacketReceiveEvent event) {
//        Player player = event.getPlayer();
//        ParkourPlayer parkourPlayer = plugin.getParkourController().getParkour(player);
//
//        // Si el jugador no está en parkour, ignora el paquete
//        if (parkourPlayer == null) return;
//
//        // Detecta si el jugador intenta romper un bloque (LEFT_CLICK/BLOCK_DIG)
//        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
//            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);
//
//            // Obtiene la posición del bloque clickeado
//            Vector3i blockPos = packet.getBlockPosition();
//            Location clickedLoc = new Location(
//                    player.getWorld(),
//                    blockPos.getX(),
//                    blockPos.getY(),
//                    blockPos.getZ()
//            );
//
//            System.out.println("risas");
//            // Verifica si el bloque está en activeBlocks
//            if (parkourPlayer.getActiveBlocks().stream()
//                    .anyMatch(loc -> loc.equals(clickedLoc))) {
//                event.setCancelled(true);
//            }
//        }
//    }

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
                    clicked.getZ() + "&7)" + "&a has been set!";

            if (selection.isFullObject()) {
                message += "&7 (&f" + selection.getCuboid().volume() + " &eblocks" +
                        "&7)";
            }
            ChatUtil.sendMessage(player, message);
        }
    }
}
