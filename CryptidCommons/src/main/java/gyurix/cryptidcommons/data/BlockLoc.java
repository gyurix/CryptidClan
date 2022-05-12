package gyurix.cryptidcommons.data;

import gyurix.cryptidcommons.conf.StringSerializable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

@Getter
public class BlockLoc implements StringSerializable {
    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public BlockLoc(Block b) {
        this.world = b.getWorld().getName();
        this.x = b.getX();
        this.y = b.getY();
        this.z = b.getZ();
    }

    public BlockLoc(String in) {
        String[] d = in.split(" ", 4);
        world = d[0];
        x = Integer.parseInt(d[1]);
        y = Integer.parseInt(d[2]);
        z = Integer.parseInt(d[3]);
    }

    public Block toBlock() {
        return Bukkit.getWorld(world).getBlockAt(x, y, z);
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x + 0.5, y, z + 0.5);
    }

    @Override
    public String toString() {
        return world + " " + x + " " + y + " " + z;
    }
}
