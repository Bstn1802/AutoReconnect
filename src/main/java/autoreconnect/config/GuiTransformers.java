package autoreconnect.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.BaseListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListListEntry;

import java.lang.reflect.Field;
import java.util.List;

public final class GuiTransformers {
    private GuiTransformers() { }

    @SuppressWarnings("rawtypes")
    public static List<AbstractConfigListEntry> setMinimum(List<AbstractConfigListEntry> guis, int minimum) {
        for (AbstractConfigListEntry gui : guis) {
            if (gui instanceof IntegerListEntry entry) {
                entry.setMinimum(minimum);
            } else if (gui instanceof IntegerListListEntry entry) {
                entry.setMinimum(minimum);
            }
        }
        return guis;
    }

    @SuppressWarnings("rawtypes")
    public static List<AbstractConfigListEntry> disableInsertInFront(List<AbstractConfigListEntry> guis) {
        for (AbstractConfigListEntry gui : guis) {
            if (gui instanceof BaseListEntry) {
                try {
                    Field insertInFront = BaseListEntry.class.getDeclaredField("insertInFront");
                    if (insertInFront.canAccess(gui) || insertInFront.trySetAccessible()) {
                        insertInFront.set(gui, false);
                    }
                } catch (NoSuchFieldException | IllegalAccessException ignore) { }
            }
        }
        return guis;
    }

    public static boolean isField(Field field, Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName).equals(field);
        } catch (NoSuchFieldException ignore) { }
        return false;
    }
}