package doubledev.beac.checks.impl.combat.killaura;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import doubledev.beac.BEAC;
import doubledev.beac.checks.Check;
import doubledev.beac.checks.CheckType;
import doubledev.beac.data.PlayerData;
import doubledev.beac.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class KillAuraA extends Check {

    public KillAuraA() {
        super("KillAura (A)", true, CheckType.COMBAT, false, 10);

        protocol.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PlayerData data = dataManager.getPlayerData(event.getPlayer());
                if(data == null) return;

                if(System.currentTimeMillis() - data.lastTeleport < 500) return;

                if (System.currentTimeMillis() - data.lastAttackEntityTime >= 10000) return;

                BEAC.getInstance().getServer().getRegionScheduler().execute(BEAC.getInstance(), event.getPlayer().getLocation(), () -> {
                    WrappedEnumEntityUseAction wrappedAction = event.getPacket().getEnumEntityUseActions().read(0);
                    EnumWrappers.EntityUseAction action = wrappedAction.getAction();

                    Entity entity = event.getPacket().getEntityModifier(event.getPlayer().getWorld()).read(0);

                    if(action != EnumWrappers.EntityUseAction.ATTACK || !(entity instanceof LivingEntity attackedEntity)) return;

                    List<Location> pastAttackedEntityLocations = data.attackedEntityLocations.getEstimatedLocation(data.ping, 300).stream().map(detailedLocation -> detailedLocation.toLocation(event.getPlayer().getWorld())).toList();

//                    debug(PlayerUtils.wasLookingAtHitbox(event.getPlayer(), attackedEntity, pastAttackedEntityLocations) + "");

                    if(!PlayerUtils.wasLookingAtHitbox(event.getPlayer(), attackedEntity, pastAttackedEntityLocations)) {
                        if(++data.killauraAThreshold > 15) {
                            flag(event.getPlayer());
                            event.setCancelled(true);
                            data.killauraAThreshold = 10;
                        }
                    }
                });
            }
        });
    }
}
