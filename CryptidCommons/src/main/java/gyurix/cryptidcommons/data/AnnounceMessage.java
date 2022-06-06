package gyurix.cryptidcommons.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static gyurix.cryptidcommons.util.StrUtils.fillVariables;

public class AnnounceMessage {
    public String actionbar, chat, subtitle, title;

    public void broadcast(Object... vars) {
        if (!chat.isEmpty()) {
            String msg = fillVariables(chat, vars);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(msg);
            }
        }
        if (!actionbar.isEmpty()) {
            String msg = fillVariables(actionbar, vars);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendActionBar(msg);
            }
        }
        if (!title.isEmpty()) {
            String msg = fillVariables(title, vars);
            String msgSub = fillVariables(subtitle, vars);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(msg, msgSub, 5, 30, 5);
            }
        }
    }
}
