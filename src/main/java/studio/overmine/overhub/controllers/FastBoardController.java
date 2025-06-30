package studio.overmine.overhub.controllers;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.listeners.FastBoardListener;
import studio.overmine.overhub.models.resources.types.ScoreboardResource;
import studio.overmine.overhub.models.scoreboard.FastBoard;
import studio.overmine.overhub.models.scoreboard.FastBoardAdapter;
import studio.overmine.overhub.models.scoreboard.FastBoardAnimation;
import studio.overmine.overhub.models.scoreboard.FastBoardThread;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class FastBoardController {

    private final OverHub plugin;
    private final Map<UUID, FastBoard> boards;
    private FastBoardAdapter adapter;
    private FastBoardThread thread;
    private FastBoardListener listener;
    private FastBoardAnimation titleAnimation;
    private FastBoardAnimation footerAnimation;

    public FastBoardController(OverHub plugin) {
        this.plugin = plugin;
        this.boards = new HashMap<>();
        this.setup();
    }

    public void createBoardPlayer(Player player) {
        FastBoard board = new FastBoard(player);
        boards.put(player.getUniqueId(), board);
    }

    public void removeBoardPlayer(Player player) {
        FastBoard board = boards.remove(player.getUniqueId());
        if (board != null) board.delete();
    }

    public void setup() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }

        this.onReload();
        this.thread = new FastBoardThread(this);
    }

    public void onDisable() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    public void onReload() {
        this.titleAnimation = new FastBoardAnimation(ScoreboardResource.SCOREBOARD_TITLE_ANIMATION_LINES, ScoreboardResource.SCOREBOARD_TITLE_ANIMATION_INTERVAL);
        this.footerAnimation = new FastBoardAnimation(ScoreboardResource.SCOREBOARD_FOOTER_ANIMATION_LINES, ScoreboardResource.SCOREBOARD_FOOTER_ANIMATION_INTERVAL);
    }
}
