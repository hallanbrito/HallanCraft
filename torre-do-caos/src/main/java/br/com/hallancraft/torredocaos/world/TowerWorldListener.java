package br.com.hallancraft.torredocaos.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

/** Managed-world protection: lobby and arena previews are immutable until gameplay safety is ready. */
public final class TowerWorldListener implements Listener {
    private final JavaPlugin plugin;
    private final TowerWorldService worlds;
    public TowerWorldListener(JavaPlugin plugin, TowerWorldService worlds) {
        this.plugin = plugin;
        this.worlds = worlds;
    }
    private boolean managed(Block block) { return worlds.manages(block.getWorld()); }

    @EventHandler(priority = EventPriority.MONITOR)
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!worlds.isLobbyReady()) return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && worlds.isLobbyReady()) {
                player.teleport(worlds.lobby());
                player.sendMessage("§6§lTorre do Caos §7| Lobby seguro. Use §e/torre jogar §7para prévia da arena.");
            }
        });
    }

    @EventHandler public void quit(PlayerQuitEvent event) {
        // Preview-only: no persistent run. F1.5 will own disconnect/reconnect sessions.
        worlds.allocator().release(event.getPlayer().getUniqueId());
    }

    @EventHandler public void respawn(PlayerRespawnEvent event) {
        if (worlds.manages(event.getPlayer().getWorld()) && worlds.isLobbyReady()) {
            worlds.allocator().release(event.getPlayer().getUniqueId());
            event.setRespawnLocation(worlds.lobby());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void teleport(PlayerTeleportEvent event) {
        Location destination = event.getTo();
        if (destination == null) return;
        if (worlds.manages(destination.getWorld())) {
            if (!worlds.withinLobby(destination)
                && !worlds.withinAssigned(event.getPlayer().getUniqueId(), destination))
                event.setCancelled(true);
        } else if (worlds.manages(event.getFrom().getWorld())
                && !event.getPlayer().hasPermission("torredocaos.admin")) {
            // No returning regular players to vanilla worlds via /home, /tpa or portals.
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void move(PlayerMoveEvent event) {
        Location to = event.getTo();
        if (to == null || !worlds.manages(to.getWorld())) return;
        if (event.getFrom().getBlockX() == to.getBlockX()
                && event.getFrom().getBlockY() == to.getBlockY()
                && event.getFrom().getBlockZ() == to.getBlockZ()) return;
        if (worlds.withinLobby(to) || worlds.withinAssigned(event.getPlayer().getUniqueId(), to)) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && worlds.isLobbyReady()) player.teleport(worlds.lobby());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void damage(EntityDamageEvent event) {
        if (worlds.manages(event.getEntity().getWorld())) event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event) { if (managed(event.getBlock())) event.setCancelled(true); }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void place(BlockPlaceEvent event) { if (managed(event.getBlockPlaced())) event.setCancelled(true); }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void entityExplode(EntityExplodeEvent event) { event.blockList().removeIf(this::managed); }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockExplode(BlockExplodeEvent event) { event.blockList().removeIf(this::managed); }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void burn(BlockBurnEvent event) { if (managed(event.getBlock())) event.setCancelled(true); }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void ignite(BlockIgniteEvent event) { if (managed(event.getBlock())) event.setCancelled(true); }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void flow(BlockFromToEvent event) {
        if (managed(event.getBlock()) || managed(event.getToBlock())) event.setCancelled(true);
    }
}
