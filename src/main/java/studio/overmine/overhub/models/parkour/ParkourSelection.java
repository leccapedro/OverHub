package studio.overmine.overhub.models.parkour;

import com.cryptomorin.xseries.XMaterial;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.utilities.cuboid.Cuboid;
import studio.overmine.overhub.utilities.item.ItemBuilder;

@Data
public class ParkourSelection {

    public static final ItemStack SELECTION_WAND;
    private static final String SELECTION_METADATA_KEY = "parkour_selection";

    static {
        SELECTION_WAND = new ItemBuilder(XMaterial.GOLDEN_AXE.parseMaterial())
                .setDisplayName("&aParkour Wand")
                .setLore("&7Use this item to select each corner,"
                        , "&7both as high corner and low corner")
                .build();
    }

    private Location point1;
    private Location point2;

    /**
     * Private, so that we can create a new instance in the Selection#createOrGetSelection method.
     */
    private ParkourSelection() {
    }

    /**
     * Selections are stored in the player's metadata. This method removes the need to active Bukkit Metadata API calls
     * all over the place.
     * <p>
     * This method can be modified structurally as needed, the plugin only accepts Selection objects via this method.
     *
     * @param player the player for whom to grab the Selection object for
     *
     * @return selection object, either new or created
     */
    public static ParkourSelection createOrGetSelection(OverHub plugin, Player player) {
        if (player.hasMetadata(SELECTION_METADATA_KEY)) {
            return (ParkourSelection) player.getMetadata(SELECTION_METADATA_KEY).get(0).value();
        }

        ParkourSelection selection = new ParkourSelection();

        player.setMetadata(SELECTION_METADATA_KEY, new FixedMetadataValue(plugin, selection));

        return selection;
    }

    /**
     * @return the cuboid
     */
    public Cuboid getCuboid() {
        return new Cuboid(point1, point2);
    }

    /**
     * @return if the Selection can form a full cuboid object
     */
    public boolean isFullObject() {
        return point1 != null && point2 != null;
    }

    /**
     * Resets both locations in the Selection
     */
    public void clear() {
        point1 = null;
        point2 = null;
    }
}

