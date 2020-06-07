package main.tools;

import javafx.scene.media.Media;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

public class Tools {
    public static String secondsToMmss(int seconds) {
        int s = seconds % 60;
        int m = (seconds/60)%60;
        return m+":"+String.format("%02d",s);
    }
}
