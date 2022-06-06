package gyurix.bountysystem.data;

import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import com.nftworlds.wallet.objects.Wallet;
import gyurix.cryptidcommons.data.AnnounceMessage;
import gyurix.cryptidcommons.util.StrUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gyurix.bountysystem.BountyManager.save;
import static gyurix.bountysystem.conf.ConfigManager.conf;

@Getter
@NoArgsConstructor
public class BountyPlayer {
    private int deaths;
    private int kills;
    private int streak;
    private UUID uuid;

    public BountyPlayer(UUID uuid) {
        this.uuid = uuid;
        save(this);
    }

    public String getName() {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    private void giveBounty(Player plr) {
        Map.Entry<Integer, List<String>> commands = conf.commandRewards.floorEntry(streak);
        if (commands != null) {
            for (String cmd : commands.getValue()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        StrUtils.fillVariables(cmd, "player", getName(),
                                "streak", streak,
                                "killer", plr.getName()));
            }
        }
        Map.Entry<Integer, Double> wrld = conf.wrldRewards.floorEntry(streak);
        if (wrld != null) {
            Wallet wallet = NFTPlayer.getByUUID(plr.getUniqueId()).getPrimaryWallet();
            wallet.payWRLD(wrld.getValue(), Network.POLYGON, "Bounty for killing " + getName() + " (" + streak + ")");
        }
    }

    public void incDeaths(UUID killer) {
        Player plr = Bukkit.getPlayer(killer);
        if (plr != null)
            giveBounty(plr);
        streak = 0;
        ++deaths;
        save(this);
    }

    public void incKills() {
        ++kills;
        ++streak;
        AnnounceMessage announcement = conf.announceMessages.get(streak);
        if (announcement != null)
            announcement.broadcast("player", getName());
        save(this);
    }
}
