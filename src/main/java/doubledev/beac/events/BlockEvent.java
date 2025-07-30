package doubledev.beac.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import doubledev.beac.BEAC;
import doubledev.beac.data.PlayerData;

public class BlockEvent {
    public BlockEvent() {
        BEAC.getInstance().getProtocol().addPacketListener(new PacketAdapter(BEAC.getInstance(), PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PlayerData data = BEAC.getInstance().getData().getPlayerData(event.getPlayer());
                if(data == null) return;

                EnumWrappers.PlayerDigType digType = event.getPacket().getPlayerDigTypes().read(0);

                if(digType == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
                    data.digging = true;
                } else if(digType == EnumWrappers.PlayerDigType.ABORT_DESTROY_BLOCK || digType == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK) {
                    data.digging = false;
                }
            }
        });
    }
}
