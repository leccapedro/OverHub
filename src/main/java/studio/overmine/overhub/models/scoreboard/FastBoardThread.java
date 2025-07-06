package studio.overmine.overhub.models.scoreboard;

import lombok.Setter;
import studio.overmine.overhub.controllers.FastBoardController;
import studio.overmine.overhub.utilities.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class FastBoardThread extends Thread {

    private final FastBoardController fastBoardController;
    @Setter private boolean running = true;

    public FastBoardThread(FastBoardController fastBoardController) {
        this.fastBoardController = fastBoardController;
        this.start();
    }

    @Override
    public void run() {
        while (running) {
            FastBoardAdapter adapter = fastBoardController.getAdapter();

            for (FastBoard board : fastBoardController.getBoards().values()) {
                if (board == null) continue;

                Player player = board.getPlayer();
                String title = ChatUtil.placeholder(player, adapter.getTitle(player));
                board.updateTitle(title);

                List<String> newLines = adapter.getLines(player);
                board.updateLines(newLines);
            }

            fastBoardController.getTitleAnimation().tick();
            fastBoardController.getFooterAnimation().tick();

            try {
                sleep(50L * 2L);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to update scoreboard - " + e.getMessage());
            }
        }
    }
}

