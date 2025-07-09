package studio.overmine.overhub.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import studio.overmine.overhub.controllers.ParkourController;
import studio.overmine.overhub.models.parkour.ParkourPlayer;

import static com.github.retrooper.packetevents.protocol.player.DiggingAction.START_DIGGING;

public class PacketEventsListener implements PacketListener {
    private final ParkourController parkourController;

    public PacketEventsListener(ParkourController parkourController) {
        this.parkourController = parkourController;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = event.getPlayer();
        if (player == null || !player.isOnline()) return;

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            handleBlockBreak(event, player);
        }

        else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            handleBlockInteract(event, player);
        }
    }

    private void handleBlockBreak(PacketReceiveEvent event, Player player) {
        ParkourPlayer parkourPlayer = parkourController.getParkour(player);
        if (parkourPlayer == null) return;

        WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);

        if (packet.getAction() != START_DIGGING) {
            return;
        }

        Location clickedLoc = getClickedLocation(packet.getBlockPosition(), player);

        if (isParkourBlock(parkourPlayer, clickedLoc)) event.setCancelled(true);
    }

    private void handleBlockInteract(PacketReceiveEvent event, Player player) {
        ParkourPlayer parkourPlayer = parkourController.getParkour(player);
        if (parkourPlayer == null) return;

        WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);
        Location clickedLoc = getClickedLocation(packet.getBlockPosition(), player);

        if (isParkourBlock(parkourPlayer, clickedLoc)) event.setCancelled(true);
    }

    private Location getClickedLocation(Vector3i position, Player player) {
        return new Location(
                player.getWorld(),
                position.getX(),
                position.getY(),
                position.getZ()
        );
    }

    private boolean isParkourBlock(ParkourPlayer parkourPlayer, Location loc) {
        return parkourPlayer.getActiveBlocks().stream()
                .anyMatch(blockLoc -> blockLoc.equals(loc));
    }
}
