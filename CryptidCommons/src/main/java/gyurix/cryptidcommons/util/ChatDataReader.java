package gyurix.cryptidcommons.util;

import gyurix.cryptidcommons.gui.GUIListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ChatDataReader {
    private final Consumer<String> con;
    private final Runnable onCancel;

    public ChatDataReader(Player plr, Consumer<String> con, Runnable onCancel) {
        this.con = con;
        this.onCancel = onCancel;
        GUIListener.chatDataReaders.put(plr.getUniqueId(), this);
    }

    public void onMessage(Player plr, String msg) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(GUIListener.registeredUnder, () -> {
            if (msg.equalsIgnoreCase("cancel")) {
                GUIListener.chatDataReaders.remove(plr.getUniqueId());
                if (onCancel != null)
                    onCancel.run();
                return;
            }
            con.accept(msg);
        });
    }
}
