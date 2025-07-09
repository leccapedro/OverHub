package studio.overmine.overhub.models.parkour;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.ParkourController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.cuboid.Cuboid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ParkourPlayer {
    private final OverHub plugin;
    private final UserController userController;
    private final ParkourController parkourController;
    private final List<Location> activeBlocks = new ArrayList<>();
    private final Player player;
    private final Cuboid cuboid;
    private int score;
    private Location previousLocation;

    public ParkourPlayer(OverHub plugin, Player player) {
        this.plugin = plugin;
        this.userController = plugin.getUserController();
        this.parkourController = plugin.getParkourController();
        this.player = player;
        this.cuboid = parkourController.getCuboid();
        this.score = 0;
    }

    public void start() {
        if (parkourController.getCuboid() == null) {
            ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_NOT_CUBOID);
            parkourController.stopParkour(player);
            return;
        }

        previousLocation = player.getLocation();
        Location startLocation = parkourController.findStartingPoint();

        if (startLocation == null) {
            plugin.getLogger().warning("Parkour: A valid starting location was not found for " + player.getName());
            ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_ERROR);
            parkourController.stopParkour(player);
            return;
        }

        activeBlocks.add(startLocation);
        parkourController.placeBlock(startLocation, player, true);

        for (int i = 0; i < 2; i++) if (!generateAndPlaceNextBlock()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(startLocation.clone().add(0.5, 1, 0.5));
                ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_START.replace("%score%",
                        String.valueOf(userController.getUser(player.getUniqueId()).getParkourHS())));
            }
        }.runTask(plugin);
    }

    public void advance() {
        score++;
        Location oldestBlock = activeBlocks.remove(0);
        player.sendBlockChange(oldestBlock, Material.AIR.createBlockData());

        if (generateAndPlaceNextBlock()) {
            player.playSound(player.getLocation(), XSound.ENTITY_CHICKEN_EGG.parseSound(), 0.5f, 1.0f);
            if (score % ConfigResource.PARKOUR_SYSTEM_STREAK_INTERVAL == 0) {
                ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_STREAK.replace("%score%", String.valueOf(score)));
                if (!ConfigResource.PARKOUR_SYSTEM_STREAK_COMMANDS.isEmpty())
                    ConfigResource.PARKOUR_SYSTEM_STREAK_COMMANDS.forEach(
                            string -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), ChatUtil.placeholder(player, string)));
            }
        }
    }

    public void stop() {
        for (Location loc : activeBlocks) {
            WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange(
                    new Vector3i(
                            loc.getBlockX(),
                            loc.getBlockY(),
                            loc.getBlockZ()), 0);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, blockChange);
        }
        activeBlocks.clear();
    }

    public Location getNextTargetBlock() {
        return activeBlocks.size() > 1 ? activeBlocks.get(1) : activeBlocks.get(0);
    }

    public double getLowestBlockHeight() {
        return activeBlocks.stream().mapToDouble(Location::getY).min().orElse(Double.MAX_VALUE);
    }

    private boolean generateAndPlaceNextBlock() {
        Location lastBlock = activeBlocks.get(activeBlocks.size() - 1);
        Location nextBlock = parkourController.generateNextBlock(lastBlock, activeBlocks,
                ConfigResource.PARKOUR_SYSTEM_GENERATOR_DISTANCE_MIN,
                ConfigResource.PARKOUR_SYSTEM_GENERATOR_DISTANCE_MAX);

        if (nextBlock == null) {
            ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_ERROR);
            parkourController.stopParkour(player);
            return false;
        }

        activeBlocks.add(nextBlock);
        parkourController.placeBlock(nextBlock, player, true);
        return true;
    }

    public boolean isNewHighScore(UUID uuid) {
        return userController.getUser(uuid).getParkourHS() < score;
    }
}