package studio.overmine.overhub.utilities;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@UtilityClass
public class SerializeUtil {

    public String serializeLocation(Location location) {
        if (location == null) return null;
        return location.getWorld().getName() + ", " +
                location.getX() + ", " +
                location.getY() + ", " +
                location.getZ() + ", " +
                location.getYaw() + ", " +
                location.getPitch();
    }

    public Location deserializeLocation(String data) {
        if (data == null || data.isEmpty()) return null;

        String[] splittedData = data.split(", ");

        if (splittedData.length < 6) return null;

        World world = Bukkit.getWorld(splittedData[0]);
        double x = Double.parseDouble(splittedData[1]);
        double y = Double.parseDouble(splittedData[2]);
        double z = Double.parseDouble(splittedData[3]);
        float yaw = Float.parseFloat(splittedData[4]);
        float pitch = Float.parseFloat(splittedData[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }
}
