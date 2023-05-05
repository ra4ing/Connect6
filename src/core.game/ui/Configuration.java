package core.game.ui;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    public static final int TIME_LIMIT;
    public static final boolean GUI;
    public static final int MAX_STEP;

    public Configuration() {
    }

    static {
        Properties pps = new Properties();

        try {
            pps.load(new FileInputStream("file.properties"));
            //pps.load(new FileInputStream("../AIContest/file.properties"));
        } catch (IOException var2) {
            var2.printStackTrace();
        }

        TIME_LIMIT = Integer.parseInt(pps.getProperty("TimeLimit"));
        GUI = Boolean.parseBoolean(pps.getProperty("GUI"));
        MAX_STEP = Integer.parseInt(pps.getProperty("MaxStep"));
    }
}
