package studio.overmine.overhub.models.resources.types;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.models.scoreboard.ScoreboardModel;
import studio.overmine.overhub.models.user.types.PvpState;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.FileConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScoreboardResource extends Resource {

    public static boolean SCOREBOARD_ENABLED, SCOREBOARD_TITLE_ANIMATION_ENABLED, SCOREBOARD_FOOTER_ANIMATION_ENABLED;
    public static long SCOREBOARD_TITLE_ANIMATION_INTERVAL, SCOREBOARD_FOOTER_ANIMATION_INTERVAL;
    public static List<String> SCOREBOARD_TITLE_ANIMATION_LINES, SCOREBOARD_FOOTER_ANIMATION_LINES;
    public static ScoreboardModel SCOREBOARD_MODEL;
    public static Map<PvpState, String> SCOREBOARD_PVP_STATE_LABELS = Collections.emptyMap();
    public static String SCOREBOARD_PVP_NOT_IN_COMBAT_MESSAGE = "You're not in combat!";
    public static String SCOREBOARD_PVP_COMBAT_TIME_COLOR = "&e";

    public ScoreboardResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig scoreboardFile = plugin.getFileConfig("scoreboard");
        FileConfiguration configuration = scoreboardFile.getConfiguration();

        SCOREBOARD_ENABLED = configuration.getBoolean("scoreboard.enabled", true);

        Map<String, ScoreboardModel.Layout> layouts = new LinkedHashMap<>();
        ConfigurationSection layoutsSection = configuration.getConfigurationSection("scoreboard.layouts");
        if (layoutsSection != null) {
            for (String key : layoutsSection.getKeys(false)) {
                ConfigurationSection layoutSection = layoutsSection.getConfigurationSection(key);
                if (layoutSection == null) {
                    continue;
                }

                List<String> lines = new ArrayList<>(layoutSection.getStringList("lines"));
                layouts.put(key.toLowerCase(Locale.ROOT), new ScoreboardModel.Layout(key, lines));
            }
        }

        String defaultLayoutKey = configuration.getString("scoreboard.default-layout", "lobby");
        SCOREBOARD_MODEL = new ScoreboardModel(SCOREBOARD_ENABLED, defaultLayoutKey, layouts);

        SCOREBOARD_TITLE_ANIMATION_ENABLED = configuration.getBoolean("title-animation.enabled", true);
        SCOREBOARD_TITLE_ANIMATION_INTERVAL = configuration.getLong("title-animation.interval", 200L);
        List<String> titleLines = configuration.getStringList("title-animation.lines");
        if (titleLines == null || titleLines.isEmpty()) {
            titleLines = Collections.singletonList("&6&lOverHub");
        }
        SCOREBOARD_TITLE_ANIMATION_LINES = ChatUtil.translate(titleLines);

        SCOREBOARD_FOOTER_ANIMATION_ENABLED = configuration.getBoolean("footer-animation.enabled", true);
        SCOREBOARD_FOOTER_ANIMATION_INTERVAL = configuration.getLong("footer-animation.interval", 3000L);
        List<String> footerLines = configuration.getStringList("footer-animation.lines");
        if (footerLines == null || footerLines.isEmpty()) {
            footerLines = Collections.singletonList("&7server.net");
        }
        SCOREBOARD_FOOTER_ANIMATION_LINES = ChatUtil.translate(footerLines);

        ConfigurationSection stateLabelsSection = configuration.getConfigurationSection("pvp.state-labels");
        Map<PvpState, String> stateLabels = new EnumMap<>(PvpState.class);
        if (stateLabelsSection != null) {
            for (PvpState state : PvpState.values()) {
                String key = state.name().toLowerCase(Locale.ROOT);
                String label = stateLabelsSection.getString(key);
                if (label != null && !label.trim().isEmpty()) {
                    stateLabels.put(state, ChatUtil.translate(label));
                }
            }
        }
        SCOREBOARD_PVP_STATE_LABELS = Collections.unmodifiableMap(stateLabels);

        String notInCombat = configuration.getString("pvp.not-in-combat-message");
        if (notInCombat != null && !notInCombat.trim().isEmpty()) {
            SCOREBOARD_PVP_NOT_IN_COMBAT_MESSAGE = ChatUtil.translate(notInCombat);
        }

        String combatTimeColor = configuration.getString("pvp.combat-time-color", "&e");
        if (combatTimeColor != null && !combatTimeColor.trim().isEmpty()) {
            SCOREBOARD_PVP_COMBAT_TIME_COLOR = ChatUtil.translate(combatTimeColor);
        }
    }
}