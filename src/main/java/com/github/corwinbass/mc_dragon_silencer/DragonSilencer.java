package com.github.corwinbass.mc_dragon_silencer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DragonSilencer extends JavaPlugin implements Listener {

    private PacketAdapter Adapter = null;

    @Override
    public void onEnable() {
        try {
            this.Adapter = new PacketAdapter(this, ListenerPriority.NORMAL,
                    PacketType.Play.Server.WORLD_EVENT) {
                public void onPacketSending(PacketEvent event) {
                    if (event.getPacketType() == PacketType.Play.Server.WORLD_EVENT) {
                        if (event.getPacket().getIntegers().read(0) == 1028) {
                            event.setCancelled(true);  // block the packet, prevents playing the sound on whole server
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
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getLocation().getWorld().getEnvironment() == World.Environment.THE_END)) return;
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            Location loc = event.getEntity().getLocation();
            loc.getWorld().getPlayers().forEach(player -> player.playSound(loc, Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f));
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
