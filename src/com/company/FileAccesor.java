package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class FileAccesor {
    public static void carregarEstadistiques() throws SQLException, IOException {
        FileReader file = new FileReader("C:\\Users\\Anomu\\Desktop\\");
        BufferedReader br = new BufferedReader(file);
        br.readLine();


    }
}
