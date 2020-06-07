package main.tools;

public class Tools {
    public static String secondsToMmss(int seconds) {
        int s = seconds % 60;
        int m = (seconds/60)%60;
        return m+":"+String.format("%02d",s);
    }
}
