package gyurix.cryptidgear.data;

import lombok.Getter;
import net.minecraft.core.particles.ParticleType;

@Getter
public class Ability {
    private int cooldown;
    private double damage;
    private int duration;
    private String effectType;
    private int level;
    private double radius;
    private AbilityType type;
    private String particle;
}
