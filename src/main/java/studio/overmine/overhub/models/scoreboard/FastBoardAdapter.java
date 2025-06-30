package studio.overmine.overhub.models.scoreboard;

import org.bukkit.entity.Player;

import java.util.List;

public interface FastBoardAdapter {

    String getTitle(Player player);
    List<String> getLines(Player player);
}
