package doubledev.beac.checks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import doubledev.beac.BEAC;
import doubledev.beac.data.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;

public class Check implements Listener {
    private final String name;
    private final CheckType type;
    private boolean enabled;
    private final boolean punishable;
    private final int max;

    public Plugin plugin = BEAC.getInstance();
    public ProtocolManager protocol = BEAC.getInstance().getProtocol();
    public DataManager dataManager = BEAC.getInstance().getData();

    public Map<Player, Integer> violations = new WeakHashMap<>();

    public Check(String name, boolean enabled, CheckType type, boolean punishable, int max) {
        this.name = name;
        this.enabled = enabled;
        this.type = type;
        this.punishable = punishable;
        this.max = max;

        BEAC.getInstance().getServer().getPluginManager().registerEvents(this, BEAC.getInstance());
    }

    public void flag(Player player, String... information) {
        int violation = this.violations.getOrDefault(player, 0) + 1;
        NamedTextColor violationC;
        if(violation < max / 3) {
            violationC = NamedTextColor.GREEN;
        } else if(violation < max / 3 * 2) {
            violationC = NamedTextColor.YELLOW;
        } else {
            violationC = NamedTextColor.DARK_RED;
        }
        if(information.length > 0) {
            StringBuilder formattedInfo = new StringBuilder();
            for (int i = 0; i < information.length; i++) {
                formattedInfo.append(information[i]);
                if (i < information.length - 1) {
                    formattedInfo.append(",");
                }
            }
            for (Player staff : BEAC.getInstance().getServer().getOnlinePlayers()) {
                if (staff.hasPermission("beac.staff")) {
                    Component notif = Component.text("[").color(NamedTextColor.DARK_GRAY)
                            .append(Component.text("BEAC").color(NamedTextColor.RED),
                                    Component.text("] ").color(NamedTextColor.DARK_GRAY),
                                    Component.text(player.getName()).color(violationC),
                                    Component.text(" đã bị phát hiện sử dụng ").color(NamedTextColor.RED),
                                    Component.text(getName()).color(NamedTextColor.YELLOW),
                                    Component.text(" [").color(NamedTextColor.DARK_GRAY),
                                    Component.text(formattedInfo.toString()).color(NamedTextColor.WHITE),
                                    Component.text("] ").color(NamedTextColor.DARK_GRAY),
                                    Component.text(" (").color(NamedTextColor.DARK_GRAY),
                                    Component.text("x" + violation).color(violationC),
                                    Component.text(") ").color(NamedTextColor.DARK_GRAY));
                    staff.sendMessage(notif);
                    BEAC.getInstance().getServer().getConsoleSender().sendMessage(notif);
                    staff.playSound(staff.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
            }
        } else {
            for (Player staff : BEAC.getInstance().getServer().getOnlinePlayers()) {
                if (staff.hasPermission("beac.staff")) {
                    Component notif = Component.text("[").color(NamedTextColor.DARK_GRAY)
                            .append(Component.text("BEAC").color(NamedTextColor.RED),
                                    Component.text("] ").color(NamedTextColor.DARK_GRAY),
                                    Component.text(player.getName()).color(violationC),
                                    Component.text(" đã bị phát hiện sử dụng ").color(NamedTextColor.RED),
                                    Component.text(getName()).color(NamedTextColor.YELLOW),
                                    Component.text(" (").color(NamedTextColor.DARK_GRAY),
                                    Component.text("x" + violation).color(violationC),
                                    Component.text(") ").color(NamedTextColor.DARK_GRAY));
                    staff.sendMessage(notif);
                    BEAC.getInstance().getServer().getConsoleSender().sendMessage(notif);
                    staff.playSound(staff.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
            }
        }

        if(violation > getMax()) {
            if(isPunishable()) {
                player.kick();
                violations.put(player, 0);
            } else {
                player.showTitle(Title.title(Component.text("Bạn đã bị kick!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD), Component.empty()));
            }
        }
        this.violations.put(player, violation);
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isPunishable() {
        return punishable;
    }

    public int getMax() {
        return max;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public CheckType getType() {
        return type;
    }

    public void debug(String... information) {
        StringBuilder formattedInfo = new StringBuilder();
        for (int i = 0; i < information.length; i++) {
            formattedInfo.append(information[i]);
            if (i < information.length - 1) {
                formattedInfo.append(",");
            }
        }
        BEAC.getInstance().getServer().broadcast(Component.text("[Debug-BEAC] " + getName() + ": " + formattedInfo.toString()));
    }
}
