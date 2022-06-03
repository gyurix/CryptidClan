package gyurix.bountysystem.conf;

import gyurix.bountysystem.data.AnnounceMessage;
import gyurix.cryptidcommons.db.MySQLDatabase;
import lombok.Getter;

import java.util.List;
import java.util.TreeMap;

@Getter
public class Config {
    public TreeMap<Integer, AnnounceMessage> announceMessages;
    public TreeMap<Integer, List<String>> commandRewards;
    public int damageExpireMs;
    public MySQLDatabase mySQL;
    public TreeMap<Integer, Double> wrldRewards;
}
