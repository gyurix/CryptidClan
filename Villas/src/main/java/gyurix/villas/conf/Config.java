package gyurix.villas.conf;

import gyurix.cryptidcommons.db.MySQLDatabase;
import gyurix.cryptidcommons.gui.GUIConfig;
import gyurix.villas.data.Group;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class Config {
    private String nonMemberGroup;
    private MySQLDatabase mySQL;
    private HashMap<String, Group> groups;
    private HashMap<String, GUIConfig> guis;
}
