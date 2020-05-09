package main.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Exchange {
    public static final String HELLO = "HELLO";

    public static final String BYE = "BYE";

    public static final String GET_CLIENTS = "GETCLIENTS";

    public static final String LIST_FILES = "LISTFILES";

    public static final String LIST_ACTIONS = "LISTACTIONS";

    public static final String ALIVE = "ALIVE";

    public Exchange() {
    }

    public static List<String> getListOfAllStates() {
        List<String> list = new ArrayList<String>();

        for (Field field : Exchange.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                list.add(field.getName());
            }
        }
        return list;
    }
}

