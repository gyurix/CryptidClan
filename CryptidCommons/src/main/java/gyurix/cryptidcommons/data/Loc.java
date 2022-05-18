package gyurix.cryptidcommons.data;

import gyurix.cryptidcommons.conf.StringSerializable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import static gyurix.cryptidcommons.util.StrUtils.DF;

@Getter
public class Loc implements StringSerializable {
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private float yaw, pitch;

    public Loc(Block b) {
        this.world = b.getWorld().getName();
        this.x = b.getX();
        this.y = b.getY();
        this.z = b.getZ();
    }

    public Loc(Location loc) {
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public Loc(String in) {
        String[] d = in.split(" ", 6);
        world = d[0];
        x = Double.parseDouble(d[1]);
        y = Double.parseDouble(d[2]);
        z = Double.parseDouble(d[3]);
        yaw = Float.parseFloat(d[4]);
        pitch = Float.parseFloat(d[5]);
    }

    public Block toBlock() {
        return Bukkit.getWorld(world).getBlockAt((int) x, (int) y, (int) z);
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return world + " " + DF.format(x) + " " + DF.format(y) + " " + DF.format(z) + " " + DF.format(yaw) + " " + DF.format(pitch);
    }
}
