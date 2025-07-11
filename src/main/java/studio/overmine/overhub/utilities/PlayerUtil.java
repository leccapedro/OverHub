package studio.overmine.overhub.utilities;

import com.cryptomorin.xseries.particles.XParticle;
import lombok.experimental.UtilityClass;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import studio.overmine.overhub.OverHub;

/**
 * @author Risas
 * @date 10-07-2025
 * @discord https://risas.me/discord
 */

@UtilityClass
public class PlayerUtil {

    public void spawnParticle(World world, Location location, String particleName) {
        if (OverHub.getVersion() <= 8) {
            world.playEffect(location, Effect.valueOf(particleName), 5);
        }
        else {
            world.spawnParticle(XParticle.valueOf(particleName).get(), location, 1, 0.1, 0.1, 0.1, 0.5);
        }
    }
}
