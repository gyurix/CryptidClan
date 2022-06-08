package gyurix.cryptidgear.conf;

import gyurix.cryptidgear.data.Weapon;
import lombok.Getter;

import java.util.TreeMap;

@Getter
public class Config {
    TreeMap<String, Weapon> weapons;
}
