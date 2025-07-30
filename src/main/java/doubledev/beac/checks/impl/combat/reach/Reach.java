package doubledev.beac.checks.impl.combat.reach;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import doubledev.beac.BEAC;
import doubledev.beac.checks.Check;
import doubledev.beac.checks.CheckType;
import doubledev.beac.data.PlayerData;
import doubledev.beac.utils.DetailedLocation;
import doubledev.beac.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class Reach extends Check {
    public Reach() {
        super("Reach", true, CheckType.COMBAT, false, 20);

        protocol.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                BEAC.getInstance().getServer().getRegionScheduler().execute(BEAC.getInstance(), event.getPlayer().getLocation(), () -> {
                    PlayerData data = dataManager.getPlayerData(event.getPlayer());
                    if(data == null) return;

                    if(System.currentTimeMillis() - data.lastTeleport < 500) return;

                    Entity entity = event.getPacket().getEntityModifier(event.getPlayer().getWorld()).read(0);

                    if(!(entity instanceof LivingEntity attackedEntity)) return;

                    List<Vector> pastAttackedEntityLocations = data.attackedEntityLocations.getEstimatedLocation(data.ping, 150).stream().map(DetailedLocation::toVector).toList();

                    if(attackedEntity != data.lastAttackEntity) {
                        data.attackedEntityLocations.clear();
                        return;
                    }

                    double distance = pastAttackedEntityLocations.stream().mapToDouble(vector -> PlayerUtils.distanceToBox(event.getPlayer(), attackedEntity, vector, data.ping)).min().orElse(0);

                    if(distance > event.getPlayer().getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getBaseValue()) {
                        if(++data.reachThreshold > 10) {
                            flag(event.getPlayer(), String.format("distance=%.2f", distance));
                            event.setCancelled(true);
                        }
                    } else data.reachThreshold = Math.max(--data.reachThreshold, 0);
                });
            }
        });
    }
}
