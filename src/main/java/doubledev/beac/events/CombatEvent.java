package doubledev.beac.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import doubledev.beac.BEAC;
import doubledev.beac.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class CombatEvent {
    public CombatEvent() {
        BEAC.getInstance().getProtocol().addPacketListener(new PacketAdapter(BEAC.getInstance(), PacketType.Play.Client.KEEP_ALIVE, PacketType.Play.Server.KEEP_ALIVE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PlayerData data = BEAC.getInstance().getData().getPlayerData(event.getPlayer());
                if (data == null) return;

                data.lastServerKeepAlive = System.currentTimeMillis();
            }

            @Override
            public void onPacketReceiving(PacketEvent event) {
                PlayerData data = BEAC.getInstance().getData().getPlayerData(event.getPlayer());
                if (data == null) return;

                data.ping = System.currentTimeMillis() - data.lastServerKeepAlive;
            }
        });

        BEAC.getInstance().getProtocol().addPacketListener(new PacketAdapter(BEAC.getInstance(), PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PlayerData data = BEAC.getInstance().getData().getPlayerData(event.getPlayer());
                if (data == null) return;

                BEAC.getInstance().getServer().getRegionScheduler().execute(BEAC.getInstance(), event.getPlayer().getLocation(), () -> {
                    WrappedEnumEntityUseAction wrappedAction = event.getPacket().getEnumEntityUseActions().read(0);
                    EnumWrappers.EntityUseAction action = wrappedAction.getAction();

                    Entity entity = event.getPacket().getEntityModifier(event.getPlayer().getWorld()).read(0);

                    if(action != EnumWrappers.EntityUseAction.ATTACK || !(entity instanceof LivingEntity)) return;

                    data.lastAttackEntity = (LivingEntity) entity;
                    data.lastAttackEntityTime = System.currentTimeMillis();
                });
            }
        });
    }
}
