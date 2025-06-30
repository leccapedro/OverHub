package studio.overmine.overhub.utilities;

import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import studio.overmine.overhub.OverHub;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProxyUtil {

    public void sendServer(OverHub plugin, Player player, String server) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ConnectOther");
            out.writeUTF(player.getName());
            out.writeUTF(server);

            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
        catch (Exception exception) {
            plugin.getLogger().warning("Error while sending " + player.getName() + " to " + server + ": " + exception.getMessage());
        }
    }
}
