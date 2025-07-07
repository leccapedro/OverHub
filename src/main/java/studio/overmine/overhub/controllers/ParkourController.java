package studio.overmine.overhub.controllers;

import com.cryptomorin.xseries.particles.XParticle;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.parkour.ParkourPlayer;
import studio.overmine.overhub.models.parkour.ParkourSelection;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.FileConfig;
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
    private static final int[] HEIGHT_VARIATIONS = {-1, 0, 1};
    private static final double EXPLOSION_RADIUS = 0.7;

    public ParkourController(OverHub plugin) {
        this.plugin = plugin;
        this.userController = plugin.getUserController();
        this.parkourConfig = plugin.getFileConfig("parkour");
        this.parkours = new HashMap<>();
        this.random = ThreadLocalRandom.current();
    }

    public void loadOrRefresh() {
        Location l1 = SerializeUtil.deserializeLocation(parkourConfig.getString("cuboid.higher"));
        Location l2 = SerializeUtil.deserializeLocation(parkourConfig.getString("cuboid.lower"));
        if (l1 != null && l2 != null) this.cuboid = new Cuboid(l1, l2);
    }

    public void setCuboid(Player player) {
        ParkourSelection selection = ParkourSelection.createOrGetSelection(plugin, player);
        if (selection.isFullObject()) {
            this.cuboid = selection.getCuboid();
            ChatUtil.sendMessage(player, "&aParkour cuboid has been updated!");
            parkourConfig.getConfiguration().set("parkour.cuboid.higher", SerializeUtil.serializeLocation(cuboid.getUpperCorner()));
            parkourConfig.getConfiguration().set("parkour.cuboid.lower", SerializeUtil.serializeLocation(cuboid.getLowerCorner()));
            parkourConfig.save();
            parkourConfig.reload();
            selection.clear();
        } else {
            ChatUtil.sendMessage(player, "&cPlease select a valid higher and lower locations.");
        }
    }

    public void startParkourPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (parkours.containsKey(uuid)) {
            ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_ALREADY);
            return;
        }

        parkours.put(uuid, new ParkourPlayer(plugin));
        Bukkit.getScheduler().runTask(plugin, () -> parkours.get(uuid).start(player));
        ChatUtil.sendMessage(player, LanguageResource.PARKOUR_MESSAGE_START
                .replace("%score%", String.valueOf(userController.getUser(uuid).getParkourScore())));
    }

    public void stopParkour(Player player) {
        ParkourPlayer parkour = parkours.remove(player.getUniqueId());
        if (parkour != null) parkour.stop();
    }

    public void onDisable() {
        parkours.values().forEach(ParkourPlayer::stop);
        parkours.clear();
    }

    public void placeBlockAt(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        List<Material> blockMaterials = generator.getBlockMaterials();
        Material blockType = blockMaterials.get(random.nextInt(blockMaterials.size()));
        world.getBlockAt(location).setType(blockType);
        spawnExplosionEffect(location, 1);
    }

    public Location generateNextBlock(Location previousBlock) {
        World world = previousBlock.getWorld();
        int x = previousBlock.getBlockX();
        int y = previousBlock.getBlockY();
        int z = previousBlock.getBlockZ();

        int minX = cuboid.getLowerX(), maxX = cuboid.getUpperX();
        int minZ = cuboid.getLowerZ(), maxZ = cuboid.getUpperZ();
        int minY = cuboid.getLowerY(), maxY = cuboid.getUpperY();
        double minDistSquared = Math.pow(generator.getMinDistance(), 2);

        int maxAttempts = generator.getMaxPlacementAttempts();
        int distance = getRandomDistance(random);
        int newX = x, newZ = z;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            switch (DIRECTIONS[random.nextInt(DIRECTIONS.length)]) {
                case 'N':
                    newZ -= distance;
                    break;
                case 'S':
                    newZ += distance;
                    break;
                case 'E':
                    newX += distance;
                    break;
                case 'W':
                    newX -= distance;
                    break;
            }

            newX = clamp(newX, minX, maxX);
            newZ = clamp(newZ, minZ, maxZ);
            int newY = clamp(y + HEIGHT_VARIATIONS[random.nextInt(HEIGHT_VARIATIONS.length)], minY, maxY);

            if (isValidPlacement(world, newX, newY, newZ) && distanceSquared(x, z, newX, newZ) >= minDistSquared) {
                return new Location(world, newX, newY, newZ);
            }
        }

        System.out.println("&c[ParkourCube] No valid location found after multiple attempts...");
        return previousBlock;
    }

    public Location findValidStartingPoint() {
        World world = cuboid.getWorld();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int maxAttempts = generator.getMaxPlacementAttempts();
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = random.nextInt(cuboid.getLowerX(), cuboid.getUpperX() + 1);
            int y = random.nextInt(cuboid.getLowerY(), cuboid.getUpperY() + 1);
            int z = random.nextInt(cuboid.getLowerZ(), cuboid.getUpperZ() + 1);

            if (isValidPlacement(world, x, y, z)) {
                return new Location(world, x, y, z);
            }
        }
        return null;
    }

    private int getRandomDistance(ThreadLocalRandom random) {
        return generator.getMinDistance() + random.nextInt(generator.getMaxDistance() - generator.getMinDistance() + 1);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private boolean isValidPlacement(World world, int x, int y, int z) {
        return world != null && world.getChunkAt(x >> 4, z >> 4).isLoaded() &&
                world.getBlockAt(x, y, z).getType() == Material.AIR &&
                world.getBlockAt(x, y + 1, z).getType() == Material.AIR &&
                world.getBlockAt(x, y + 2, z).getType() == Material.AIR &&
                world.getBlockAt(x, y - 1, z).getType() == Material.AIR &&
                world.getBlockAt(x, y - 2, z).getType() == Material.AIR;
    }

    private double distanceSquared(int x1, int z1, int x2, int z2) {
        return Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2);
    }


    private void spawnExplosionEffect(Location location, int count) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int effectiveCount = Math.max(6, count);
        Location particleLoc = location.clone().add(0.5, 0.5, 0.5);

        for (int i = 0; i < effectiveCount; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double x = EXPLOSION_RADIUS * Math.sin(phi) * Math.cos(theta);
            double y = EXPLOSION_RADIUS * Math.sin(phi) * Math.sin(theta);
            double z = EXPLOSION_RADIUS * Math.cos(phi);

            particleLoc.add(x, y, z);
            location.getWorld().spawnParticle(XParticle.FLAME.get(), particleLoc, 3, 0.15, 0.15, 0.15, 3);
            particleLoc.subtract(x, y, z);
        }
    }
}
