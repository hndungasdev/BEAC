package doubledev.beac.events;

import doubledev.beac.BEAC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitEvent implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        if(BEAC.getInstance().getFloodgateApi().isFloodgatePlayer(event.getPlayer().getUniqueId())) {
            BEAC.getInstance().getData().add(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        if(BEAC.getInstance().getFloodgateApi().isFloodgatePlayer(event.getPlayer().getUniqueId())) {
            BEAC.getInstance().getData().remove(event.getPlayer());
        }
    }
}
