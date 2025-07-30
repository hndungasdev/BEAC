package doubledev.beac.checks.impl.combat.autoclicker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import doubledev.beac.checks.Check;
import doubledev.beac.checks.CheckType;
import doubledev.beac.data.PlayerData;

public class AutoClicker extends Check {
    public AutoClicker() {
        super("AutoClicker", true, CheckType.COMBAT, false, 10);

        protocol.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PlayerData data = dataManager.getPlayerData(event.getPlayer());
                if(data == null) return;

                if(event.getPacket().getType() == PacketType.Play.Client.USE_ENTITY) {
                    WrappedEnumEntityUseAction wrappedAction = event.getPacket().getEnumEntityUseActions().read(0);
                    EnumWrappers.EntityUseAction action = wrappedAction.getAction();

                    if(action == EnumWrappers.EntityUseAction.ATTACK) {
                        data.lastAttackEntityTime = System.currentTimeMillis();
                        data.clicks.add(System.currentTimeMillis());
                    }
                }

                if(event.getPacket().getType() == PacketType.Play.Client.ARM_ANIMATION && System.currentTimeMillis() - data.lastAttackEntityTime >= 50L) {
                    if(data.digging) return;
                    data.clicks.add(System.currentTimeMillis());
                }

                debug(String.valueOf(data.clicks.size()));
                //Data stable or click with recognizable pattern will be flag nigga
            }
        });
    }
}
