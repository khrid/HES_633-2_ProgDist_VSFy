package main.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ExchangeEnum {
    HELLO("HELLO", true, "HELLO - Establishes connection with the server"),
    BYE("BYE", false, "BYE - Terminate connection with the server"),
    GET_CLIENTS("GET_CLIENTS", false, "GET_CLIENTS - Get server's clients list"),
    LIST_FILES("LIST_FILES", false, "LIST_FILES - List client's available files to stream"),
    LIST_ACTIONS("LIST_ACTIONS", false, "LIST_ACTIONS - List available actions");

    public final String command;
    public final boolean technical;
    public final String description;

    ExchangeEnum(String code, boolean technical, String description) {
        this.command = code;
        this.technical = technical;
        this.description = description;
    }

    public static ArrayList<String> getAvailableActions() {
        ArrayList<String> list = new ArrayList<>();
        List<ExchangeEnum> enumValues = Arrays.asList(ExchangeEnum.values());
        for (ExchangeEnum exchangeEnum : enumValues) {
            if(!exchangeEnum.technical) {
               list.add(exchangeEnum.description);
            }
        }
        return list;
    }
}
