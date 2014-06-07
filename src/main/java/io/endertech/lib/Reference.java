package io.endertech.lib;

import com.google.common.base.Throwables;

import java.io.InputStream;
import java.util.Properties;

public class Reference
{
    static {
        Properties prop = new Properties();

        try {
            InputStream stream = Reference.class.getClassLoader().getResourceAsStream("version.properties");
            prop.load(stream);
            stream.close();
        } catch (Exception e) {
            Throwables.propagate(e);
        }

        VERSION_NUMBER = prop.getProperty("version");
    }

    public static final String MOD_ID = "EnderTech";
    public static final String MOD_NAME = "EnderTech";
    public static final String VERSION_NUMBER;
    public static final String FINGERPRINT = "@FINGERPRINT@";
    public static final String CHANNEL_NAME = MOD_ID;
    public static final int ONE_SECOND_IN_TICKS = 20;
}
