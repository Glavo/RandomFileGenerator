package org.glavo.rfg;

import java.util.ResourceBundle;

public final class I18N {
    public static final ResourceBundle resources = ResourceBundle.getBundle("I18N");

    public static String getString(String key) {
        return resources.getString(key);
    }

    public static String getString(String key, Object... args) {
        return String.format(resources.getString(key), args);
    }
}
