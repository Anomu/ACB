package com.company;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public class ACBMain {

    public static void main(String[] args) throws IOException, SQLException, ParseException {
        Menu menu = new Menu();
        Connection conn = null;
        Identity identity;
        int option;
        int intents = 0;
        DBAccessor dbaccessor = new DBAccessor();
        FileAccesor fileAccesor = new FileAccesor();
        dbaccessor.init();
        while (intents < 3 && conn == null) {
            identity = menu.autenticacio(intents);
            // prova de test
            identity.toString();

            conn = dbaccessor.getConnection(identity);
            intents++;
        }

        option = menu.menuPral();
        while (option > 0 && option < 12) {
            switch (option) {
                case 1:
                    dbaccessor.mostrarEquips();
                    break;

                case 2:
                    dbaccessor.mostrarJugadorsEquip();
                    break;

                case 3:
                    dbaccessor.crearEquip();
                    break;

                case 4:
                    dbaccessor.crearJugador();
                    break;

                case 5:
                    dbaccessor.crearPartit();
                    break;

                case 6:
                    dbaccessor.jugadorsSenseEquip();
                    break;

                case 7:
                    dbaccessor.assignarJugadorEquip();
                    break;

                case 8:
                    dbaccessor.desvicularJugadorDequip();
                    break;

                case 9:
                    FileAccesor.carregarEstadistiques();
                    break;

                case 10:
                    dbaccessor.sortir();
                    break;

                default:
                    System.out.println("Introdueix una de les opcions anteriors");
                    break;

            }
            option = menu.menuPral();
        }

    }

}
