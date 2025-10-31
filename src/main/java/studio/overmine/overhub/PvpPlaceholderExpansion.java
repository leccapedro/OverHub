package studio.overmine.overhub;

import java.util.List;
import java.util.Locale;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.resources.types.ScoreboardResource;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.types.PvpState;

public class PvpPlaceholderExpansion extends PlaceholderExpansion {

    private final OverHub plugin;

    public PvpPlaceholderExpansion(OverHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "pvp";
    }

    @Override
    public @NotNull String getAuthor() {
        List<String> authors = plugin.getDescription().getAuthors();
        if (authors == null || authors.isEmpty()) {
            return plugin.getDescription().getName();
        }
        return String.join(", ", authors);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) {
            return null;
        }

        String identifier = params.toLowerCase(Locale.ROOT);
        if (identifier.startsWith("pvp_")) {
            identifier = identifier.substring(4);
        }
        UserController userController = plugin.getUserController();
        User user = userController != null ? userController.getUser(offlinePlayer.getUniqueId()) : null;
        CombatPlayer combatPlayer = resolveCombatPlayer(offlinePlayer);

        switch (identifier) {
            case "enabled":
                return String.valueOf(isPvpEnabled(user, combatPlayer));
            case "state":
                return resolveStateLabel(resolveState(user, combatPlayer));
            case "combat_time_left":
                return resolveCombatTimeLeft(user, combatPlayer);
            case "kills":
                return String.valueOf(user != null ? user.getPvpKills() : 0);
            case "streak":
                return String.valueOf(user != null ? user.getPvpKillStreak() : 0);
            case "last_hit_by":
                return user != null && user.getLastHitBy() != null ? user.getLastHitBy() : "";
            default:
                return null;
        }
    }

    private String resolveCombatTimeLeft(@Nullable User user, @Nullable CombatPlayer combatPlayer) {
        if (combatPlayer != null && combatPlayer.isInCombat()) {
            return formatCombatTime(combatPlayer.getCombatTimeRemainingSeconds());
        }

        PvpState state = resolveState(user, combatPlayer);
        if (state != null) {
            String label = ScoreboardResource.SCOREBOARD_PVP_COMBAT_LEFT_LABELS.get(state);
            if (label != null) {
                return label;
            }
        }
        return "";
    }

    private String formatCombatTime(int seconds) {
        if (seconds <= 0) {
            return "00:00";
        }

        int minutes = seconds / 60;
        int remainder = seconds % 60;
        return String.format(Locale.ROOT, "%02d:%02d", minutes, remainder);
    }

    private boolean isPvpEnabled(@Nullable User user, @Nullable CombatPlayer combatPlayer) {
        if (combatPlayer != null) {
            return combatPlayer.isPvP();
        }
        return user != null && user.isPvpEnabled();
    }

    private PvpState resolveState(@Nullable User user, @Nullable CombatPlayer combatPlayer) {
        if (combatPlayer != null) {
            if (combatPlayer.isInCombat()) {
                return PvpState.COMBAT;
            }
            if (user != null && user.getPvpState() == PvpState.EXITING) {
                return PvpState.EXITING;
            }
            return combatPlayer.isPvP() ? PvpState.ACTIVE : PvpState.EQUIPPING;
        }
        return user != null ? user.getPvpState() : PvpState.INACTIVE;
    }

    private String resolveStateLabel(PvpState state) {
        if (state == null) {
            return "";
        }

        String label = ScoreboardResource.SCOREBOARD_PVP_STATE_LABELS.get(state);
        if (label != null && !label.isEmpty()) {
            return label;
        }
        return state.name().toLowerCase(Locale.ROOT);
    }

    private @Nullable CombatPlayer resolveCombatPlayer(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) {
            return null;
        }

        CombatController combatController = plugin.getCombatController();
        if (combatController == null) {
            return null;
        }

        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return null;
        }

        return combatController.getCombatPlayer(player);
    }
}