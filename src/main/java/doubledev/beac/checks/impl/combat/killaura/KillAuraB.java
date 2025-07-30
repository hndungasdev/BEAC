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
import doubledev.beac.utils.MathUtils;
import doubledev.beac.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

public class KillAuraB extends Check {

    public KillAuraB() {
        super("KillAura (B)", true, CheckType.COMBAT, false, 5);

        protocol.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PlayerData data = dataManager.getPlayerData(event.getPlayer());
                if(data == null) return;

                if(System.currentTimeMillis() - data.lastTeleport < 500) return;

                if (System.currentTimeMillis() - data.lastAttackEntityTime >= 10000) return;

                if(data.sensitivity <= 0) return;

                BEAC.getInstance().getServer().getRegionScheduler().execute(BEAC.getInstance(), event.getPlayer().getLocation(), () -> {

                    WrappedEnumEntityUseAction wrappedAction = event.getPacket().getEnumEntityUseActions().read(0);
                    EnumWrappers.EntityUseAction action = wrappedAction.getAction();

                    Entity entity = event.getPacket().getEntityModifier(event.getPlayer().getWorld()).read(0);

                    if(action != EnumWrappers.EntityUseAction.ATTACK || !(entity instanceof LivingEntity)) return;

                    List<Location> pastAttackedEntityLocations = data.attackedEntityLocations.getEstimatedLocation(data.ping, 300).stream().map(detailedLocation -> detailedLocation.toLocation(event.getPlayer().getWorld())).toList();

                    Location playerEye = event.getPlayer().getEyeLocation();
                    float pitch = event.getPlayer().getLocation().getPitch();
                    
                    for (Location targetLoc : pastAttackedEntityLocations) {
                        Vector toTarget = targetLoc.toVector().subtract(playerEye.toVector()).normalize();
                        Vector playerDirection = playerEye.getDirection().normalize();

                        double targetPitch = Math.toDegrees(Math.asin(-toTarget.getY()));
                        double playerPitch = Math.toDegrees(Math.asin(-playerDirection.getY()));

                        double pitchAngleDiff = Math.abs(targetPitch - playerPitch);

                        if (pitchAngleDiff < data.sensitivity * 0.4) {
                            data.killauraBThreshold++;
                        }
                    }

                    float deltaPitch = Math.abs(pitch - data.lastPitch);

                    data.lastPitch = pitch;

                    if (++data.killauraBThreshold >= 5 && deltaPitch > data.sensitivity * 0.8) {
                        flag(event.getPlayer());
                    } else data.killauraBThreshold = Math.min(1, --data.killauraBThreshold);

//                    debug(String.valueOf(data.sensitivity));
                });
            }
        });
    }
}
