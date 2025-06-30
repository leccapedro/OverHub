package studio.overmine.overhub.models.scoreboard;

import lombok.Getter;
import java.util.List;

@Getter
public class FastBoardAnimation {

    private final List<String> lines;
    private final long interval;
    private String current;
    private int index;
    private long ticks;

    public FastBoardAnimation(List<String> lines, long interval) {
        this.lines = lines;
        this.interval = interval;
        this.current = "";
        this.index = 0;
        this.ticks = System.currentTimeMillis();
    }

    public void tick() {
        if (ticks < System.currentTimeMillis()) {
            ticks = System.currentTimeMillis() + interval;

            if (index == lines.size()) {
                index = 0;
                current = lines.get(0);
                return;
            }

            String nextLine = lines.get(index);
            index++;
            current = nextLine;
        }
    }
}
