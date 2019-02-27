package DragonSilencer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DragonSilencer extends JavaPlugin {

    private PacketAdapter Adapter = null;

    @Override
    public void onEnable() {
        try {
            this.Adapter = new PacketAdapter(this, ListenerPriority.NORMAL,
                    PacketType.Play.Server.WORLD_EVENT) {
                public void onPacketSending(PacketEvent event) {
                    if (event.getPacketType() == PacketType.Play.Server.WORLD_EVENT) {
                        if (event.getPacket().getIntegers().read(0) == 1028) {
                            event.setCancelled(true);
                        }
                    }
                }
            };

            ProtocolLibrary.getProtocolManager().addPacketListener(this.Adapter);
            getLogger().info("DragonSilencer successfully enabled.");
        } catch(Exception ex) {
            getLogger().info("DragonSilencer - error occurred during start up.");
            ex.printStackTrace(); // .. to see what is actually wrong
        }
    }

    @Override
    public void onDisable() {
        try {
            ProtocolLibrary.getProtocolManager().removePacketListener(this.Adapter);
            this.Adapter = null;
            getLogger().info("DragonSilencer successfully disabled.");
        } catch(Exception ex) {
            getLogger().info("DragonSilencer - error occurred during disabling plugin.");
        }
    }
}
