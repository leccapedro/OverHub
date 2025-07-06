package studio.overmine.overhub.models.scoreboard;

import studio.overmine.overhub.controllers.FastBoardController;
import studio.overmine.overhub.models.resources.types.ScoreboardResource;
import studio.overmine.overhub.utilities.ChatUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FastBoardProvider implements FastBoardAdapter {

    private final FastBoardController fastBoardController;

    public FastBoardProvider(FastBoardController fastBoardController) {
        this.fastBoardController = fastBoardController;
    }

    @Override
    public String getTitle(Player player) {
        return fastBoardController.getTitleAnimation().getCurrent();
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        for (String line : ScoreboardResource.SCOREBOARD_LINES) {
            if (line.contains("%scoreboard-footer%")) {
                lines.add(fastBoardController.getFooterAnimation().getCurrent());
                continue;
            }

            lines.add(line);
        }

        return ChatUtil.placeholder(player, lines);
    }
}
