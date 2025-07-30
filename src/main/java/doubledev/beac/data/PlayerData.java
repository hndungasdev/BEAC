package doubledev.beac.data;

import doubledev.beac.BEAC;
import doubledev.beac.utils.PastLocation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {

    public Player player;

    //Misc
    public long ping, lastServerKeepAlive, lastTeleport;
    public double lastDeltaPitch = 0D;
    public List<Integer> sensitivities = new ArrayList<>();
    public int sensitivity = 0;
    public float lastPitch = 0f;
    public long lastChangeSensTime, lastMove;

    public boolean digging = false;

    //Combat
    public LivingEntity lastAttackEntity;
    public long lastAttackEntityTime;
    public PastLocation attackedEntityLocations = new PastLocation();
    public List<Long> clicks = new ArrayList<>();

    //Threshold
    public int reachThreshold, killauraAThreshold, killauraBThreshold = 3;

    public PlayerData(Player player) {
        this.player = player;

        BEAC.getInstance().getServer().getGlobalRegionScheduler().runAtFixedRate(BEAC.getInstance(), t -> {
            if(System.currentTimeMillis() - lastAttackEntityTime > 10000L) {
                lastAttackEntity = null;
            }
            if(lastAttackEntity != null && lastAttackEntity.isValid()) attackedEntityLocations.addLocation(lastAttackEntity.getLocation());

            clicks.removeIf(time -> System.currentTimeMillis() - time > 1000L);
        }, 1L, 1L);
    }
}
