package studio.overmine.overhub.models.parkour;

import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.ParkourController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.utilities.ChatUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParkourPlayer {
    private final OverHub plugin;
    private final UserController userController;
    private final ParkourController parkourController;
    private final List<Location> activeBlocks = new ArrayList<>();
    private int score;
    private Location previousLocation;

    public ParkourPlayer(OverHub plugin) {
        this.plugin = plugin;
        this.userController = plugin.getUserController();
        this.parkourController = plugin.getParkourController();
        this.score = 0;
    }

    public void start(Player player) {
        activeBlocks.clear();
        Location startLocation = parkourController.findValidStartingPoint();

        if (startLocation == null) {
            Bukkit.getLogger().warning("No valid starting location found in the area...");
            return;
        }

        previousLocation = player.getLocation();
        initializeFirstBlocks(startLocation, player);
        teleportPlayerToStart(player);
    }

    private void initializeFirstBlocks(Location startLocation, Player player) {
        activeBlocks.add(startLocation);
        parkourController.placeBlockAt(startLocation);

        for (int i = 1; i < 3; i++) {
            Location nextBlock = parkourController.generateNextBlock(activeBlocks.get(i - 1));
            if (nextBlock.equals(activeBlocks.get(i - 1))) {
                handleBlockGenerationFailure(player);
                return;
            }
            nextBlock = adjustBlockHeight(nextBlock);
            activeBlocks.add(nextBlock);
            parkourController.placeBlockAt(nextBlock);
        }
    }

    private void teleportPlayerToStart(Player player) {
        Location teleportLocation = activeBlocks.get(0).clone().add(0.5, 1, 0.5);
        Bukkit.getScheduler().runTask(plugin, () -> player.teleport(teleportLocation));
    }

    public void advance(Player player) {
        incrementScore();
        removeOldestBlock();
        Location lastBlock = activeBlocks.get(activeBlocks.size() - 1);
        Location nextBlock = parkourController.generateNextBlock(lastBlock);

        if (nextBlock.equals(lastBlock)) {
            handleBlockGenerationFailure(player);
            return;
        }

        nextBlock = adjustBlockHeight(nextBlock);
        activeBlocks.add(nextBlock);
        parkourController.placeBlockAt(nextBlock);
        player.playSound(player.getLocation(), XSound.ENTITY_CHICKEN_EGG.parseSound(), 0.5F, 1.0F);
    }

    private Location adjustBlockHeight(Location nextBlock) {
        if (nextBlock.getY() - activeBlocks.get(0).getY() > 1) {
            Location first = activeBlocks.get(0);
            Location second = activeBlocks.get(1);

            boolean isBetweenX = nextBlock.getBlockX() >= Math.min(first.getBlockX(), second.getBlockX()) &&
                    nextBlock.getBlockX() <= Math.max(first.getBlockX(), second.getBlockX());
            boolean isBetweenZ = nextBlock.getBlockZ() >= Math.min(first.getBlockZ(), second.getBlockZ()) &&
                    nextBlock.getBlockZ() <= Math.max(first.getBlockZ(), second.getBlockZ());

            if (isBetweenX || isBetweenZ) {
                return nextBlock.subtract(0, 1, 0);
            }
        }
        return nextBlock;
    }

    private void incrementScore() {
        score++;
    }

    public boolean isNewHighScore(User user) {
        return user.getParkourScore() < score;
    }

    public Location getNextTargetBlock() {
        return activeBlocks.get(1);
    }

    public double getLowestBlockHeight() {
        return activeBlocks.stream()
                .mapToDouble(Location::getY)
                .min()
                .orElse(activeBlocks.get(1).getY());
    }

    private void handleBlockGenerationFailure(Player player) {
        stop();
        ChatUtil.sendMessage(player, "&cError generating next parkour block...");
    }

    public void stop() {
        activeBlocks.forEach(location -> location.getBlock().setType(Material.AIR));
        activeBlocks.clear();
    }

    private void removeOldestBlock() {
        activeBlocks.get(0).getBlock().setType(Material.AIR);
        activeBlocks.remove(0);
    }
}

