package studio.overmine.overhub.models.user;

import lombok.Getter;

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

    VisibilityType(String id) {
        this.id = id;
    }
}
