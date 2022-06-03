package gyurix.bountysystem.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AnnounceMessage {
    public String actionbar, chat, subtitle, title;

    public void broadcast(String pln) {
        if (!chat.isEmpty()) {
            String msg = chat.replace("<player>", pln);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(msg);
            }
        }
        if (!actionbar.isEmpty()) {
            String msg = actionbar.replace("<player>", pln);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendActionBar(msg);
            }
        }
        if (!title.isEmpty()) {
            String msg = title.replace("<player>", pln);
            String msgSub = subtitle.replace("<player>", pln);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(msg, msgSub, 5, 30, 5);
            }
        }
    }
}
