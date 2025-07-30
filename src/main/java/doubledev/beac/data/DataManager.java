package doubledev.beac.data;

import doubledev.beac.BEAC;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class DataManager {
    private final Set<PlayerData> dataSet = new HashSet<>();

    public DataManager() {
        BEAC.getInstance().getServer().getOnlinePlayers().forEach(this::add);
    }

    public PlayerData getPlayerData(Player player) {
        return dataSet.stream().filter(playerData -> playerData.player == player).findFirst().orElse(null);
    }

    public void add(Player player) {
        dataSet.add(new PlayerData(player));
    }

    public void remove(Player player) {
        dataSet.removeIf(playerData -> playerData.player  == player);
    }
}
