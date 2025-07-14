package studio.overmine.overhub.models.user;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Risas
 */
@Getter
public enum VisibilityType {
    ALL("visibility-all"),
    DONATOR("visibility-donator"),
    STAFF("visibility-staff"),
    FRIEND("visibility-friend"),
    NONE("visibility-none");

    private final String id;
    @Setter private boolean enabled;

    VisibilityType(String id) {
        this.id = id;
    }

    public static VisibilityType getNext(VisibilityType current) {
        VisibilityType[] values = values();
        int length = values.length;
        int start = current.ordinal();

        for (int i = 1; i <= length; i++) {
            VisibilityType next = values[(start + i) % length];

            if (next.enabled) {
                return next;
            }
        }

        return current;
    }
}
