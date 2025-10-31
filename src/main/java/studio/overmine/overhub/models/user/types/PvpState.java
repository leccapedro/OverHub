package studio.overmine.overhub.models.user.types;

import java.util.Locale;

public enum PvpState {
    INACTIVE,
    EQUIPPING,
    ACTIVE,
    EXITING,
    COMBAT;

    public static PvpState fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return INACTIVE;
        }

        try {
            return PvpState.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return INACTIVE;
        }
    }
}