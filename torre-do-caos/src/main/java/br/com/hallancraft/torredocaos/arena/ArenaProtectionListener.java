package br.com.hallancraft.torredocaos.arena;

import org.bukkit.block.Block;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/** F1.2: the saved arena is locked. Temporary player construction is F1.3. */
public final class ArenaProtectionListener implements Listener {
    private final ArenaBlueprintService service;
    public ArenaProtectionListener(ArenaBlueprintService service) { this.service = service; }
    private boolean locked(Block b) { return service.isLocked(b); }

    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void breakBlock(BlockBreakEvent e) { if (locked(e.getBlock())) e.setCancelled(true); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void placeBlock(BlockPlaceEvent e) { if (locked(e.getBlockPlaced())) e.setCancelled(true); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void multiPlace(BlockMultiPlaceEvent e) {
        if (e.getReplacedBlockStates().stream().anyMatch(state -> locked(state.getBlock())))
            e.setCancelled(true);
    }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void entityExplode(EntityExplodeEvent e) { e.blockList().removeIf(this::locked); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void blockExplode(BlockExplodeEvent e) { e.blockList().removeIf(this::locked); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void burn(BlockBurnEvent e) { if (locked(e.getBlock())) e.setCancelled(true); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void ignite(BlockIgniteEvent e) { if (locked(e.getBlock())) e.setCancelled(true); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void fade(BlockFadeEvent e) { if (locked(e.getBlock())) e.setCancelled(true); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void grow(BlockGrowEvent e) { if (locked(e.getBlock())) e.setCancelled(true); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void spread(BlockSpreadEvent e) { if (locked(e.getBlock())) e.setCancelled(true); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void flow(BlockFromToEvent e) {
        if (locked(e.getBlock()) || locked(e.getToBlock())) e.setCancelled(true);
    }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void fallingBlock(EntityChangeBlockEvent e) { if (locked(e.getBlock())) e.setCancelled(true); }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void pistonExtend(BlockPistonExtendEvent e) {
        if (locked(e.getBlock()) || e.getBlocks().stream().anyMatch(b ->
            locked(b) || locked(b.getRelative(e.getDirection())))) e.setCancelled(true);
    }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void pistonRetract(BlockPistonRetractEvent e) {
        if (locked(e.getBlock()) || e.getBlocks().stream().anyMatch(b ->
            locked(b) || locked(b.getRelative(e.getDirection())))) e.setCancelled(true);
    }
}
