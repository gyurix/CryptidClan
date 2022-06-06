package gyurix.timedopportunities.conf;

import gyurix.cryptidcommons.data.AnnounceMessage;
import gyurix.cryptidcommons.data.BlockLoc;
import gyurix.cryptidcommons.gui.GUIConfig;
import gyurix.timedopportunities.data.Reward;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static gyurix.cryptidcommons.util.StrUtils.rand;

@Getter
public class Config {
    public TreeMap<Integer, AnnounceMessage> announceMessages;
    public volatile List<BlockLoc> locations = new ArrayList<>();
    public GUIConfig lootGUI;
    public List<Reward> rewards;
    public List<String> holoLines;
    public List<String> spawningTimes = new ArrayList<>();

    public Reward getRandomReward() {
        double v = rand.nextDouble();
        double cur = 0;
        for (Reward r : rewards) {
            cur += r.chance;
            if (cur > v)
                return r;
        }
        return null;
    }
}
