package gyurix.cryptidcommons.util;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

public class HoloUtils {
    public static final double lineDist = 0.25;

    public static void spawn(Location loc, List<String> lines, Object... vars) {
        loc = loc.clone().add(0, (lines.size() - 1) * lineDist, 0);
        for (String line : lines) {
            ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            as.setCustomName(StrUtils.fillVariables(line, vars));
            as.setCustomNameVisible(true);
            as.setGravity(false);
            as.setInvulnerable(true);
            as.setMarker(true);
            as.setVisible(false);
            loc = loc.subtract(0, lineDist, 0);
        }
    }

    public static void despawn(Location loc, int lines) {
        for (Entity ent : loc.getChunk().getEntities()) {
            if (!(ent instanceof ArmorStand))
                continue;
            double dist = ent.getLocation().distance(loc);
            double yDist = ent.getLocation().getY() - loc.getY();
            if (yDist < 0 || dist > yDist * 1.1 || yDist > lines * lineDist * 1.1)
                continue;
            ent.remove();
        }
    }
}
