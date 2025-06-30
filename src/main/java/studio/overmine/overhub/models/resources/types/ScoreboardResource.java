package studio.overmine.overhub.models.resources.types;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.utilities.FileConfig;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardResource extends Resource {

    public static boolean SCOREBOARD_ENABLED, SCOREBOARD_TITLE_ANIMATION_ENABLED, SCOREBOARD_FOOTER_ANIMATION_ENABLED;
    public static long SCOREBOARD_TITLE_ANIMATION_INTERVAL, SCOREBOARD_FOOTER_ANIMATION_INTERVAL;
    public static List<String> SCOREBOARD_LINES, SCOREBOARD_TITLE_ANIMATION_LINES, SCOREBOARD_FOOTER_ANIMATION_LINES;

    public ScoreboardResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig scoreboardFile = plugin.getFileConfig("scoreboard");
        SCOREBOARD_ENABLED = scoreboardFile.getBoolean("scoreboard.enabled", true);
        SCOREBOARD_LINES = scoreboardFile.getStringList("scoreboard.lines", new ArrayList<>());
        SCOREBOARD_TITLE_ANIMATION_ENABLED = scoreboardFile.getBoolean("title-animation.enabled", true);
        SCOREBOARD_TITLE_ANIMATION_INTERVAL = scoreboardFile.getInt("title-animation.interval", 200);
        SCOREBOARD_TITLE_ANIMATION_LINES = scoreboardFile.getStringList("title-animation.lines", new ArrayList<>());
        SCOREBOARD_FOOTER_ANIMATION_ENABLED = scoreboardFile.getBoolean("footer-animation.enabled", true);
        SCOREBOARD_FOOTER_ANIMATION_INTERVAL = scoreboardFile.getInt("footer-animation.interval", 3000);
        SCOREBOARD_FOOTER_ANIMATION_LINES = scoreboardFile.getStringList("footer-animation.lines", new ArrayList<>());
    }
}
