package com.peseoane.galaria.xestion.component;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class Puertos {

    public static void checkNmapInstalled() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        String command = os.contains("win") ? "where nmap" : "which nmap";

        Process process = Runtime.getRuntime().exec(command);
        if (process.waitFor() != 0) {
            throw new Exception("nmap is not installed on this system");
        }
    }

    public static List<List<String>> escanearPuertos(String ipAddress) throws Exception {
        checkNmapInstalled();

        List<List<String>> result = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("nmap " + ipAddress);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Si la línea comienza con un número y contiene el texto "open", la consideramos como información relevante
                if (line.matches("^\\d.*open.*")) {
                    // Dividir la línea en campos utilizando espacios en blanco como delimitador
                    String[] fields = line.trim().split("\\s+");
                    List<String> row = new ArrayList<>();
                    // Añadir cada campo a la fila
                    for (String field : fields) {
                        row.add(field);
                    }
                    // Añadir la fila a la lista de resultados
                    result.add(row);
                }
            }
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}