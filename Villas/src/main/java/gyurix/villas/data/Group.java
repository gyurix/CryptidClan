package gyurix.villas.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;

@Data
@AllArgsConstructor
public class Group {
    private boolean build;
    private boolean enter;
    private Material icon;
    private boolean info;
    private boolean manage;
    private String name;
    private boolean removable;
    private boolean see;
    private boolean tp;
    private boolean use;

    @Override
    protected Group clone() {
        return new Group(build, enter, icon, info, manage, name, removable, see, tp, use);
    }
}
