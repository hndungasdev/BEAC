package doubledev.beac.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MathUtils {
    public static double getGcd(double a, double b) {
        if(a < b) {
            return getGcd(b, a);
        }

        if(Math.abs(b) < 0.001) {
            return a;
        } else {
            return getGcd(b, a - Math.floor(a / b) * b);
        }
    }

    public static <T> Optional<T> mostCommon(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }

        return list.stream()
                .filter(Objects::nonNull) // Optional: skip nulls if desired
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public static int round(double num) {
        double decimalPart = num - Math.floor(num);
        if (decimalPart < 0.5) {
            return (int) Math.floor(num);
        } else {
            return (int) Math.ceil(num);
        }
    }

    public static double getPitchGcdFromSensitivity(int sensitivity) {
        double closestGcd = -1;
        double smallestDiff = Double.MAX_VALUE;

        for (double gcd = 0.0001; gcd <= 1.0; gcd += 0.00001) {
            double estimated = 532.55102 * Math.pow(gcd, 3)
                    - 586.16749 * Math.pow(gcd, 2)
                    + 385.89307 * gcd
                    - 1.69098;

            double diff = Math.abs(estimated - sensitivity);
            if (diff < smallestDiff) {
                smallestDiff = diff;
                closestGcd = gcd;
            }

            if (diff < 0.01) break; // early stop if very close match
        }

        return closestGcd;
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
