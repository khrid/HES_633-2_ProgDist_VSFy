package main.tools;

/**
 * Librairie qui gère différentes fonctions:
 * -temps écoulé de la lecture en cours
 */
public class Tools {

    /**
     * Méthode qui retourne un nombre de seconde en une chaîne de caractères des minutes/secondes
     * @param seconds les secondes à convertir
     * @return une chaîne de caractères des minutes/secondes
     */
    public static String secondsToMmss(int seconds) {
        int s = seconds % 60;
        int m = (seconds/60)%60;
        return m+":"+String.format("%02d",s);
    }
}
