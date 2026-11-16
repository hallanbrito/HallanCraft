package br.com.hallancraft.torredocaos.arena;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/** Safe console/operator blueprint controls; no OP is granted to players. */
public final class ArenaAdminCommand implements CommandExecutor {
    private final ArenaBlueprintService service;
    public ArenaAdminCommand(ArenaBlueprintService service) { this.service = service; }
    @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("torredocaos.admin")) {
            sender.sendMessage("Sem permissão torredocaos.admin.");
            return true;
        }
        if (args.length != 3 || !"admin".equalsIgnoreCase(args[0])) {
            sender.sendMessage("Uso: /torre admin <exportarena|verificarena> <id>");
            return true;
        }
        if ("exportarena".equalsIgnoreCase(args[1])) service.export(sender, args[2]);
        else if ("verificarena".equalsIgnoreCase(args[1])) service.verify(sender, args[2]);
        else sender.sendMessage("Ação inválida. Use exportarena ou verificarena.");
        return true;
    }
}
