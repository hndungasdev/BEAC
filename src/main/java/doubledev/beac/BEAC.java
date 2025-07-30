package doubledev.beac;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import doubledev.beac.checks.Check;
import doubledev.beac.checks.CheckManager;
import doubledev.beac.checks.CheckType;
import doubledev.beac.data.DataManager;
import doubledev.beac.events.BlockEvent;
import doubledev.beac.events.CombatEvent;
import doubledev.beac.events.JoinQuitEvent;
import doubledev.beac.events.MoveEvents;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class BEAC extends JavaPlugin {

    private static BEAC instance;

    private FloodgateApi floodgateApi;
    private ProtocolManager protocolMan;
    private CheckManager checkMan;
    private DataManager dataMan;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("========================BEAC========================");
        getLogger().info("");
        long startTime = System.currentTimeMillis();
        getLogger().info("Loading Floodgate API...");
        floodgateApi = FloodgateApi.getInstance();
        getLogger().info("Loading ProtocolLib...");
        protocolMan = ProtocolLibrary.getProtocolManager();
        getLogger().info("Loading checks...");
        dataMan = new DataManager();

        //Loading events
        getServer().getPluginManager().registerEvents(new JoinQuitEvent(), this);
        getServer().getPluginManager().registerEvents(new MoveEvents(), this);
        new BlockEvent();
        new CombatEvent();

        checkMan = new CheckManager();
        getLogger().info("Available checks: ");
        Map<CheckType, List<Check>> checksByType = checkMan.getChecks().stream()
                .collect(Collectors.groupingBy(Check::getType));

        for(CheckType type : CheckType.values()) {
            getLogger().info("    - " + type.getName());
            checksByType.getOrDefault(type, Collections.emptyList())
                    .forEach(check -> getLogger().info("        + " + check.getName()));
        }

        getLogger().info("Done in " + (System.currentTimeMillis() - startTime) + " millisecond!");
        getLogger().info("");
        getLogger().info("====================================================");

//        protocolMan.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.getInstance()) {
//            @Override
//            public void onPacketReceiving(PacketEvent event) {
//                getLogger().info(event.getPacket().getType().name());
//            }
//        });
    }

    @Override
    public void onDisable() {
        getLogger().info("========================BEAC========================");
        getLogger().info("");
        getLogger().info("Unloading components...");
        HandlerList.unregisterAll(this);
        getServer().getGlobalRegionScheduler().cancelTasks(this);
        getLogger().info("");
        getLogger().info("====================================================");
    }

    public static BEAC getInstance() {
        return instance;
    }

    public FloodgateApi getFloodgateApi() {
        return floodgateApi;
    }

    public ProtocolManager getProtocol() {
        return protocolMan;
    }

    public DataManager getData() {
        return dataMan;
    }

    public CheckManager getCheck() {
        return checkMan;
    }
}
