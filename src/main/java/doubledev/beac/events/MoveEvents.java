package doubledev.beac.events;

import doubledev.beac.BEAC;
import doubledev.beac.data.PlayerData;
import doubledev.beac.utils.MathUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MoveEvents implements Listener {
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        PlayerData data = BEAC.getInstance().getData().getPlayerData(event.getPlayer());

        if(data == null) return;

        data.lastTeleport = System.currentTimeMillis();
    }

    @EventHandler
    public void onRotation(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        if (Math.abs(event.getTo().getPitch() - event.getFrom().getPitch()) < 0.0001) return;

        BEAC instance = BEAC.getInstance();
        if (instance == null || instance.getData() == null) return;

        PlayerData data = instance.getData().getPlayerData(event.getPlayer());
        if (data == null) return;

        double deltaPitch = Math.abs(event.getTo().getPitch() - event.getFrom().getPitch());
        float pitchGcd = (float) MathUtils.getGcd(deltaPitch, data.lastDeltaPitch);
        int sensitivity = MathUtils.round(532.55102F * Math.pow(pitchGcd, 3) - 586.16749F * Math.pow(pitchGcd, 2) + 385.89307F * pitchGcd - 1.69098F);

        if(data.sensitivities.size() > 10) {
            if(Math.abs(sensitivity - data.sensitivities.getLast()) > 2 && (System.currentTimeMillis() - data.lastChangeSensTime < 5000 || System.currentTimeMillis() - data.lastMove < 1000 || System.currentTimeMillis() - data.lastAttackEntityTime < 3000)) {
                data.lastChangeSensTime = System.currentTimeMillis();
                return;
            }
        }

        if (data.sensitivities.size() > 20) {
            MathUtils.mostCommon(data.sensitivities).ifPresent(t -> {
                if(data.sensitivity != t) {
                    data.sensitivity = t;
                    data.lastChangeSensTime = System.currentTimeMillis();
                }
            });
            data.sensitivities.removeFirst();
        }
        data.sensitivities.add(sensitivity);

        data.lastDeltaPitch = deltaPitch;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        if(event.getFrom().getX() == event.getTo().getX() &&
                event.getFrom().getY() == event.getTo().getY() &&
                event.getFrom().getZ() == event.getTo().getZ()) return;

        PlayerData data = BEAC.getInstance().getData().getPlayerData(event.getPlayer());

        if(data == null) return;

        data.lastMove = System.currentTimeMillis();
    }
}
