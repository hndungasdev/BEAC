package doubledev.beac.utils;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PastLocation {
    private final List<DetailedLocation> previousLocations = new CopyOnWriteArrayList<>();

    public DetailedLocation getPreviousLocation(long time) {
        return previousLocations.stream().min(Comparator.comparingLong(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time)))).orElse(previousLocations.get(previousLocations.size() - 1));
    }

    public List<DetailedLocation> getEstimatedLocation(long time, long delta) {
        List<DetailedLocation> locs = new ArrayList<>();

        previousLocations.stream()
                .sorted(Comparator.comparingLong(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time))))
                .filter(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time)) < delta)
                .forEach(locs::add);
        return locs;
    }

    public void addLocation(Location location) {
        if (previousLocations.size() >= 20) {
            previousLocations.removeFirst();
        }

        previousLocations.add(new DetailedLocation(location));
    }

    public void addLocation(DetailedLocation location) {
        if (previousLocations.size() >= 20) {
            previousLocations.removeFirst();
        }

        previousLocations.add(location);
    }

    public List<DetailedLocation> getPreviousLocations() {
        return previousLocations;
    }

    public void clear() {
        previousLocations.clear();
    }
}
