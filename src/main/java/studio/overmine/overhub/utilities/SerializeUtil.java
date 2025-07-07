package studio.overmine.overhub.utilities;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import studio.overmine.overhub.utilities.cuboid.Cuboid;

@UtilityClass
public class SerializeUtil {

    public String serializeLocation(Location location) {
        if (location == null || location.getWorld() == null) return null;
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


    public String serializeCuboid(Cuboid cuboid) {
        if (cuboid == null) return null;
        return cuboid.getWorld().getName() + ":" +
                cuboid.getX1() + ":" +
                cuboid.getY1() + ":" +
                cuboid.getZ1() + ":" +
                cuboid.getX2() + ":" +
                cuboid.getY2() + ":" +
                cuboid.getZ2();
    }

    public Cuboid deserializeCuboid(String data) {
        if (data == null) return null;

        String[] splittedData = data.split(":");

        if (splittedData.length < 7) return null;

        World world = Bukkit.getWorld(splittedData[0]);
        if (world == null) throw new IllegalArgumentException("World not found: " + splittedData[0]);

        int x1 = Integer.parseInt(splittedData[1]);
        int y1 = Integer.parseInt(splittedData[2]);
        int z1 = Integer.parseInt(splittedData[3]);
        int x2 = Integer.parseInt(splittedData[4]);
        int y2 = Integer.parseInt(splittedData[5]);
        int z2 = Integer.parseInt(splittedData[6]);

        return new Cuboid(world, x1, y1, z1, x2, y2, z2);
    }
}
