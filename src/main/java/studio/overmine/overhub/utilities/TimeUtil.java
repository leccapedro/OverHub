package studio.overmine.overhub.utilities;

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class TimeUtil {

    public String getTimeFormattedMillis(long millis) {
        if (millis < 1000L) return "0 second";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder builder = new StringBuilder();

        if (days > 0L) {
            builder
                    .append(days)
                    .append(" day")
                    .append(days > 1L ? "s " : " ");
        }
        if (hours > 0L) {
            builder
                    .append(hours)
                    .append(" hour")
                    .append(hours > 1L ? "s " : " ");
        }
        if (minutes > 0L) {
            builder
                    .append(minutes)
                    .append(" minute")
                    .append(minutes > 1L ? "s " : " ");
        }
        if (seconds > 0L) {
            builder
                    .append(seconds)
                    .append(" second")
                    .append(seconds > 1L ? "s " : " ");
        }
        return builder.toString().trim();
    }

    public long parseMillis(String input) {
        if (input == null || input.isEmpty()) return -1L;

        long result = 0L;
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                String str;
                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += convertMillis(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }
        return result;
    }

    private long convertMillis(int value, char unit) {
        switch (unit) {
            case 'y': {
                return value * TimeUnit.DAYS.toMillis(365L);
            }
            case 'M': {
                return value * TimeUnit.DAYS.toMillis(30L);
            }
            case 'd': {
                return value * TimeUnit.DAYS.toMillis(1L);
            }
            case 'h': {
                return value * TimeUnit.HOURS.toMillis(1L);
            }
            case 'm': {
                return value * TimeUnit.MINUTES.toMillis(1L);
            }
            case 's': {
                return value * TimeUnit.SECONDS.toMillis(1L);
            }
            default: {
                return -1L;
            }
        }
    }
}
