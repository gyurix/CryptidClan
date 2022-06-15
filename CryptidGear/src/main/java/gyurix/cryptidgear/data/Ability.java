package gyurix.cryptidgear.data;

import lombok.Getter;
import org.bukkit.Particle;

@Getter
public class Ability {
    private int cooldown;
    private double damage;
    private int durationSeconds;
    private String effectType;
    private int level;
    private Particle particle;
    private double radius;
    private AbilityType type;

    @Override
    public String toString() {
        return type.toString(this);
    }
}
