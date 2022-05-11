package gyurix.villas;

import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MoveDetector implements Runnable {
    public HashMap<String, Location> oldLoc = new HashMap<>();

    public boolean cancelMove(Player plr, Location to) {
        Villa villa = VillaManager.getVillaAt(to);
        return villa != null && !villa.hasPermission(plr, Group::isEnter);
    }

    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            String pln = p.getName();
            Location old = oldLoc.get(pln);
            Location cur = p.getLocation();
            if (old == null) {
                oldLoc.put(pln, cur);
                continue;
            }
            if (old.distance(cur) < 0.5)
                continue;
            if (cancelMove(p, cur)) {
                if (cancelMove(p, old))
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + p.getName());
                else
                    p.teleport(old);
                continue;
            }
            oldLoc.put(pln, cur);
        }
    }
}
