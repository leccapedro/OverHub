package studio.overmine.overhub.models.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.controllers.FastBoardController;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.resources.types.ScoreboardResource;
import studio.overmine.overhub.utilities.ChatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FastBoardProvider implements FastBoardAdapter {

    private static final String PVP_MODE_LAYOUT_KEY = "pvpmode";

    private final OverHub plugin;
    private final FastBoardController fastBoardController;

    public FastBoardProvider(FastBoardController fastBoardController) {
        this(Objects.requireNonNull(fastBoardController, "fastBoardController").getPlugin(), fastBoardController);
    }

    public FastBoardProvider(OverHub plugin, FastBoardController fastBoardController) {
        this.fastBoardController = Objects.requireNonNull(fastBoardController, "fastBoardController");
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public String getTitle(Player player) {
        return fastBoardController.getTitleAnimation().getCurrent();
    }

    @Override
    public List<String> getLines(Player player) {
        ScoreboardModel model = ScoreboardResource.SCOREBOARD_MODEL;
        if (model == null) {
            return Collections.emptyList();
        }

        ScoreboardModel.Layout layout = resolveLayout(model, player);
        if (layout == null) {
            return Collections.emptyList();
        }

        List<String> lines = new ArrayList<>();
        for (String rawLine : layout.getLines()) {
            String withFooter = applyFooter(rawLine);
            lines.add(withFooter);
        }

        if (lines.isEmpty()) {
            return lines;
        }

        return ChatUtil.placeholder(player, lines);
    }

    private ScoreboardModel.Layout resolveLayout(ScoreboardModel model, Player player) {
        if (isPlayerInPvpMode(player)) {
            ScoreboardModel.Layout pvpLayout = model.getLayout(PVP_MODE_LAYOUT_KEY);
            if (pvpLayout != null) {
                return pvpLayout;
            }
        }
        ScoreboardModel.Layout defaultLayout = model.getDefaultLayout();
        if (defaultLayout != null) {
            return defaultLayout;
        }
        Map<String, ScoreboardModel.Layout> layouts = model.getLayouts();
        return layouts.values().stream().findFirst().orElse(null);
    }

    private boolean isPlayerInPvpMode(Player player) {
        CombatController combatController = plugin.getCombatController();
        if (combatController != null) {
            CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
            if (combatPlayer != null && combatPlayer.isPvP()) {
                return true;
            }
        }

        String placeholderValue = ChatUtil.placeholder(player, "%pvp_enabled%");
        return isTruthy(placeholderValue);
    }

    private boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }

        String normalized = ChatColor.stripColor(value).trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty() || normalized.equals("%pvp_enabled%")) {
            return false;
        }

        switch (normalized) {
            case "true":
            case "1":
            case "on":
            case "yes":
            case "si":
            case "sí":
            case "enabled":
            case "enable":
            case "activo":
            case "activado":
                return true;
            default:
                break;
        }

        try {
            return Double.parseDouble(normalized) > 0;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private String applyFooter(String line) {
        if (line == null || line.isEmpty()) {
            return line;
        }

        FastBoardAnimation footerAnimation = fastBoardController.getFooterAnimation();
        String footer = footerAnimation != null ? footerAnimation.getCurrent() : "";
        return line.replace("%scoreboard-footer%", footer);
    }
}