package gyurix.cryptidgear.data;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerData {
    @Setter
    private long strikeUntil;
    private Ability strike;
    private HashMap<String, Long> nextWeaponAbilityUse = new HashMap<>();
    private UUID uuid;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }
}
