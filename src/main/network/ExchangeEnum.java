package main.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Structures des commandes utilisateur et technique
 * Les commandes utilisateur sont celle mise à disposition dans l'interface console
 */
public enum ExchangeEnum {
    HELLO("HELLO", true, "HELLO - Establishes connection with the server"),
    GET_CLIENTS("GET_CLIENTS", true, "GET_CLIENTS - Get server's clients list"),
    LIST_FILES("LIST_FILES", true, "LIST_FILES - List client's available files to stream"),
    BYE("BYE", false, "BYE - Terminate connection with the server"),
    LIST_ACTIONS("LIST_ACTIONS", false, "LIST_ACTIONS - List available actions"),
    PLAY("PLAY", false, "PLAY - Plays file from another client"),
    STOP("STOP", false, "STOP - Stops the current stream"),
    NOW_PLAYING("NOW_PLAYING", false, "NOW_PLAYING - Display information about current stream"),
    PAUSE("PAUSE", false, "PAUSE - Pauses the current stream"),
    RESUME("RESUME", false, "RESUME - Resumes the current stream");

    public final String command;
    public final boolean technical;
    public final String description;


    ExchangeEnum(String code, boolean technical, String description) {
        this.command = code;
        this.technical = technical;
        this.description = description;
    }

    /**
     * Retourne la liste des fonctions utilisateur disponibles
     * @return un ArrayListe de chaîne de caractères des actions disponibles
     */
    public static ArrayList<String> getAvailableActions() {
        ArrayList<String> list = new ArrayList<>();
        List<ExchangeEnum> enumValues = Arrays.asList(ExchangeEnum.values());
        for (ExchangeEnum exchangeEnum : enumValues) {
            if(!exchangeEnum.technical) {
               list.add(exchangeEnum.command);
            }
        }
        return list;
    }
}
