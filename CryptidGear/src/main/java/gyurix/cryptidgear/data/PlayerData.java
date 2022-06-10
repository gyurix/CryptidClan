package gyurix.cryptidgear.data;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class PlayerData {
    private HashMap<String, Long> nextWeaponAbilityUse = new HashMap<>();
    private Ability strike;
    @Setter
    private long strikeUntil;
    private UUID uuid;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }
}
