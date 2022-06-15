package gyurix.cryptidgear;

import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.cryptidcommons.util.StrUtils;
import gyurix.cryptidgear.data.Weapon;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static gyurix.cryptidcommons.util.StrUtils.DF;
import static gyurix.cryptidgear.conf.ConfigManager.conf;
import static gyurix.cryptidgear.conf.ConfigManager.msg;

public class CommandGear implements CommandExecutor {
    public void cmdGive(CommandSender sender, String[] args) {
        if (args.length == 1) {
            msg.msg(sender, "missing.player");
            return;
        }
        if (args.length == 2) {
            msg.msg(sender, "missing.component");
            return;
        }
        Player p = Bukkit.getPlayer(args[1]);
        if (p == null) {
            msg.msg(sender, "wrong.player", "player", args[1]);
            return;
        }
        ItemStack is = conf.getCustomItems().get(args[2]);
        if (is == null) {
            msg.msg(sender, "wrong.component", "component", args[2]);
            return;
        }
        ItemUtils.addItem(p.getInventory(), is);
        msg.msg(sender, "give", "item", ItemUtils.getName(is), "player", p.getName());
    }

    public void cmdHelp(CommandSender sender) {
        msg.msg(sender, "help");
    }

    public void cmdInfo(CommandSender sender, String[] args) {
        if (args.length == 1) {
            msg.msg(sender, "missing.component");
            return;
        }
        ItemStack is = conf.getCustomItems().get(args[1]);
        if (is == null) {
            msg.msg(sender, "wrong.component", "component", args[1]);
            return;
        }
        Weapon weapon = conf.getWeapons().get(args[1]);
        if (weapon == null) {
            msg.msg(sender, "info.component",
                    "name", args[1],
                    "dname", is.getItemMeta().getDisplayName(),
                    "type", StrUtils.toCamelCase(is.getType().name()),
                    "lore", StringUtils.join(is.getLore(), "\n"));
            return;
        }
        msg.msg(sender, "info.weapon",
                "ability", weapon.getAbility(),
                "damage", DF.format(weapon.getDamage()),
                "particle", StrUtils.toCamelCase(weapon.getAbility().getParticle().name()),
                "name", args[1],
                "dname", is.getItemMeta().getDisplayName(),
                "type", StrUtils.toCamelCase(is.getType().name()),
                "lore", StringUtils.join(is.getLore(), "\n"));
    }

    public void cmdList(CommandSender sender) {
        msg.msg(sender, "list", "components", StringUtils.join(conf.getCustomItems().keySet(), ", "));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (!sender.hasPermission("cryptidgear.admin")) {
            msg.msg(sender, "noperm");
            return true;
        }
        String sub = args.length == 0 ? "help" : args[0].toLowerCase();
        switch (sub) {
            case "help" -> cmdHelp(sender);
            case "give" -> cmdGive(sender, args);
            case "info" -> cmdInfo(sender, args);
            case "list" -> cmdList(sender);
        }
        return true;
    }
}
