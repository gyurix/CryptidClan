package gyurix.villas.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;

import java.lang.reflect.Field;

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

    public boolean isFlagEnabled(String name) {
        try {
            Field f = getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (boolean) f.get(this);
        } catch (NoSuchFieldException e) {
            return false;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
