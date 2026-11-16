package br.com.hallancraft.torredocaos.world;

import br.com.hallancraft.torredocaos.arena.ArenaAdminCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Lobby navigation and isolated floor-one preview; not a gameplay/run implementation. */
public final class TowerLobbyCommand implements CommandExecutor {
    private final TowerWorldService worlds;
    private final ArenaAdminCommand admin;
    public TowerLobbyCommand(TowerWorldService worlds, ArenaAdminCommand admin) {
        this.worlds = worlds;
        this.admin = admin;
    }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && "admin".equalsIgnoreCase(args[0])) {
            if (!worlds.isReady()) {
                sender.sendMessage("Mundos em preparacao; admin de blueprints temporariamente bloqueado.");
                return true;
            }
            return admin.onCommand(sender, command, label, args);
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Uso no jogo: /torre jogar, /torre lobby, /torre status. Admin: /torre admin ...");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage("§eComandos: /torre jogar | /torre lobby | /torre status");
            return true;
        }
        if ("status".equalsIgnoreCase(args[0])) {
            player.sendMessage("§6Torre do Caos: §f" + (worlds.isReady() ? "12 torres disponíveis" : "preparando os mundos")
                + " §7| §f" + worlds.allocator().occupied() + " §7ocupadas. Ondas inativas.");
            return true;
        }
        if ("lobby".equalsIgnoreCase(args[0]) || "sair".equalsIgnoreCase(args[0])) {
            if (!worlds.isLobbyReady()) { player.sendMessage("§cLobby ainda não está pronto."); return true; }
            if (player.teleport(worlds.lobby())) {
                worlds.allocator().release(player.getUniqueId());
                player.sendMessage("§aVocê voltou ao lobby e liberou a torre de prévia.");
            }
            return true;
        }
        if ("jogar".equalsIgnoreCase(args[0])) {
            if (!worlds.isReady()) {
                player.sendMessage("§eAguarde a geração inicial das torres. /torre status");
                return true;
            }
            var assigned = worlds.allocator().reserve(player.getUniqueId());
            if (assigned.isEmpty()) {
                player.sendMessage("§cTodas as torres de prévia estão ocupadas. Tente novamente mais tarde.");
                return true;
            }
            var tower = assigned.get();
            if (!player.teleport(worlds.spawn(tower))) {
                worlds.allocator().release(player.getUniqueId());
                player.sendMessage("§cNão foi possível entrar na torre.");
                return true;
            }
            player.sendMessage("§aArena privada " + tower.id()
                + " §7| Prévia do nível 1. Combate, drops e progressão ainda não foram ativados.");
            return true;
        }
        player.sendMessage("§eComandos: /torre jogar | /torre lobby | /torre status");
        return true;
    }
}
