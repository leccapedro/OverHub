package studio.overmine.overhub.models.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScoreboardModel {

    private final boolean enabled;
    private final String defaultLayoutKey;
    private final Map<String, Layout> layouts;
    public ScoreboardModel(boolean enabled, String defaultLayoutKey,
                           Map<String, Layout> layouts) {
        this.enabled = enabled;
        this.defaultLayoutKey = defaultLayoutKey == null ? "lobby" : defaultLayoutKey.toLowerCase(Locale.ROOT);
        this.layouts = Collections.unmodifiableMap(new LinkedHashMap<>(layouts));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDefaultLayoutKey() {
        return defaultLayoutKey;
    }

    public Map<String, Layout> getLayouts() {
        return layouts;
    }

    public Layout getLayout(String key) {
        if (key == null) {
            return null;
        }
        return layouts.get(key.toLowerCase(Locale.ROOT));
    }

    public Layout getDefaultLayout() {
        Layout layout = getLayout(defaultLayoutKey);
        if (layout != null) {
            return layout;
        }
        return layouts.values().stream().findFirst().orElse(null);
    }

    public boolean hasLayout(String key) {
        return getLayout(key) != null;
    }

    public static class Layout {

        private final String key;
        private final List<String> lines;

        public Layout(String key, List<String> lines) {
            this.key = key;
            if (lines == null || lines.isEmpty()) {
                this.lines = Collections.emptyList();
            } else {
                this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
            }
        }

        public String getKey() {
            return key;
        }

        public List<String> getLines() {
            return lines;
        }
    }
}
