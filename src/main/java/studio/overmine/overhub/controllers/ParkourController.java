package studio.overmine.overhub.controllers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.parkour.ParkourPlayer;
import studio.overmine.overhub.models.parkour.ParkourSelection;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.FileConfig;
import studio.overmine.overhub.utilities.PlayerUtil;
import studio.overmine.overhub.utilities.SerializeUtil;
import studio.overmine.overhub.utilities.cuboid.Cuboid;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class ParkourController {

    private final OverHub plugin;
    private final FileConfig parkourConfig;
    private final UserController userController;
    private final Map<UUID, ParkourPlayer> parkours;
    private Cuboid cuboid;
    private final ThreadLocalRandom random;
    private static final char[] DIRECTIONS = {'N', 'S', 'E', 'W'};
    private static final double EXPLOSION_RADIUS = 0.7;
    private static final int[] HEIGHT_VARIATIONS = {-1, 0, 1};

    public ParkourController(OverHub plugin) {
        this.plugin = plugin;
        this.userController = plugin.getUserController();
        this.parkourConfig = plugin.getFileConfig("parkour");
        this.parkours = new HashMap<>();
        this.random = ThreadLocalRandom.current();
        this.loadOrRefresh();
    }

    public void loadOrRefresh() {
        try {
            this.cuboid = SerializeUtil.deserializeCuboid(parkourConfig.getString("parkour"));
            onDisable();
        } catch (Exception e) {
            plugin.getLogger().severe("Error load Parkour: " + e.getMessage());
        }
    }

    public void setCuboid(Player player) {
        ParkourSelection selection = ParkourSelection.createOrGetSelection(plugin, player);

        if (selection.isFullObject()) {
            this.cuboid = selection.getCuboid();

            ChatUtil.sendMessage(player, "&aParkour area has been updated!");
            parkourConfig.getConfiguration().set("parkour", SerializeUtil.serializeCuboid(cuboid));
            parkourConfig.save();
            parkourConfig.reload();
            selection.clear();
        }
        else {
            ChatUtil.sendMessage(player, "&cPlease select a valid higher and lower locations.");
        }
    }

    public void startParkour(Player player) {
        parkours.put(player.getUniqueId(), new ParkourPlayer(plugin, player));
        getParkour(player).start();
    }

    public ParkourPlayer getParkour(Player player) {
        return parkours.get(player.getUniqueId());
    }

    public void stopParkour(Player player) {
        ParkourPlayer parkour = parkours.remove(player.getUniqueId());

        if (parkour != null) {
            parkour.stop();
        }
    }

    public void onDisable() {
        parkours.values().forEach(ParkourPlayer::stop);
        parkours.clear();
    }

    public Location findStartingPoint() {
        World world = plugin.getServer().getWorld(cuboid.getWorld().getName());

        if (world == null) {
            plugin.getLogger().warning("Parkour cuboid world not found.");
            return null;
        }

        int x = random.nextInt(cuboid.getLowerX(), cuboid.getUpperX() + 1);
        int y = random.nextInt(cuboid.getLowerY(), cuboid.getUpperY() - 2);
        int z = random.nextInt(cuboid.getLowerZ(), cuboid.getUpperZ() + 1);

        return new Location(world, x, y, z);
    }

    public Location generateNextBlock(Location previousBlock, List<Location> activeBlocks,
                                      int minDistance, int maxDistance) {
        World world = previousBlock.getWorld();
        if (world == null) return null;

        int x = previousBlock.getBlockX();
        int y = previousBlock.getBlockY();
        int z = previousBlock.getBlockZ();

        int minX = cuboid.getLowerX(), maxX = cuboid.getUpperX();
        int minY = cuboid.getLowerY(), maxY = cuboid.getUpperY();
        int minZ = cuboid.getLowerZ(), maxZ = cuboid.getUpperZ();

        int maxAttempts = ConfigResource.PARKOUR_SYSTEM_GENERATOR_ATTEMPTS;

        Location directionCheckBlock1 = null;
        Location directionCheckBlock2 = null;
        if (activeBlocks.size() >= 2) {
            directionCheckBlock1 = activeBlocks.get(activeBlocks.size() - 2);
            directionCheckBlock2 = activeBlocks.get(activeBlocks.size() - 1);
        }

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            char direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            int distance = minDistance + random.nextInt(maxDistance - minDistance + 1);

            int newX = x, newZ = z;
            switch (direction) {
                case 'N': newZ -= distance; break;
                case 'S': newZ += distance; break;
                case 'E': newX += distance; break;
                case 'W': newX -= distance; break;
            }

            int newY = y + HEIGHT_VARIATIONS[random.nextInt(HEIGHT_VARIATIONS.length)];

            if (newX < minX || newX > maxX || newY < minY || newY > maxY || newZ < minZ || newZ > maxZ) continue;

            Location candidate = new Location(world, newX, newY, newZ);

            boolean noValid = activeBlocks.stream().anyMatch(loc ->
                    loc.getBlockX() == candidate.getBlockX() &&
                            loc.getBlockZ() == candidate.getBlockZ()
            );
            if (noValid) continue;

            if (directionCheckBlock1 != null && directionCheckBlock2 != null) {
                boolean between = isBetween(directionCheckBlock1, directionCheckBlock2, candidate);

                if (between) continue;

            }

            return candidate;
        }

        plugin.getLogger().warning("A valid location was not found after " + maxAttempts + " attempts.");
        return null;
    }

    private static boolean isBetween(Location directionCheckBlock1, Location directionCheckBlock2, Location candidate) {
        int x1 = directionCheckBlock1.getBlockX();
        int z1 = directionCheckBlock1.getBlockZ();
        int x2 = directionCheckBlock2.getBlockX();
        int z2 = directionCheckBlock2.getBlockZ();

        int xC = candidate.getBlockX();
        int zC = candidate.getBlockZ();

        boolean isBetween = false;

        if (x1 == x2 && xC == x1) {
            if ((z1 < zC && zC < z2) || (z2 < zC && zC < z1)) {
                isBetween = true;
            }
        }

        else if (z1 == z2 && zC == z1) {
            if ((x1 < xC && xC < x2) || (x2 < xC && xC < x1)) {
                isBetween = true;
            }
        }
        return isBetween;
    }

    public void placeBlock(Location location, Player player) {
        Material blockType = ConfigResource.PARKOUR_SYSTEM_GENERATOR_BLOCKS.get(
                random.nextInt(ConfigResource.PARKOUR_SYSTEM_GENERATOR_BLOCKS.size())
        );

        StateType stateType = StateTypes.getByName(blockType.getKey().getKey());

        if (stateType == null) {
            Bukkit.getLogger().warning("[Parkour] StateType for " + blockType.name() + " not found.");
            return;
        }

        WrappedBlockState blockState = WrappedBlockState.getDefaultState(stateType);
        int blockId = blockState.getGlobalId();

        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(new Vector3i(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()), blockId);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);

        spawnExplosionEffect(location);
    }

    private void spawnExplosionEffect(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        Location particleLocation = location.clone().add(0.5, 0.5, 0.5);

        for (int i = 0; i < 6; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double x = EXPLOSION_RADIUS * Math.sin(phi) * Math.cos(theta);
            double y = EXPLOSION_RADIUS * Math.sin(phi) * Math.sin(theta);
            double z = EXPLOSION_RADIUS * Math.cos(phi);

            particleLocation.add(x, y, z);
            particleLocation.subtract(x, y, z);

            PlayerUtil.spawnParticle(world, particleLocation, "CLOUD");
        }
    }
}
