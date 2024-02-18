package psp.unidad01.practica112.esclava;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class ModaEsclavaApp {

    public static void main(String[] args) throws IOException {
        /*
         * Recibe los valores del maestro
         */
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            
            // Lee la línea de entrada que contiene los valores enviados por el maestro
            String linea = br.readLine();
            
            // Convierte la línea de entrada en un mapa de números y sus repeticiones
            Map<Integer, Integer> mapaNumeros = ficheroToMapa(linea);
            
            // Imprime el mapa en formato clave=valor en la salida estándar
            String salida = "";
            for (Map.Entry<Integer, Integer> entry : mapaNumeros.entrySet()) {
                salida = salida + entry.getKey() + "=" + entry.getValue() + " ";
            }
            
            // Escribe la salida en la salida estándar
            BufferedWriter escritor = new BufferedWriter(new OutputStreamWriter(System.out));
            escritor.write(salida);
            escritor.newLine();
            escritor.flush();
            escritor.close();
            System.exit(0); // Termina el proceso con código de salida 0 (éxito)
            
        } catch (Exception e) {
            System.exit(-1); // Termina el proceso con código de salida -1 (error)
        }
    }

    /**
     * Metodo que mete los valores en un mapa sin duplicados
     * @param ficheroInt
     * @return Mapa que contiene los números como claves y sus repeticiones como valores
     */
    public static Map<Integer, Integer> ficheroToMapa(String linea) {
        Map<Integer, Integer> mapaNumeros = new HashMap<>();
        
        // Divide la línea en números separados por "/"
        for (String numero : linea.split("/")) {
            // Convierte cada número en un entero
            int num = Integer.parseInt(numero);
            // Verifica si el número ya está en el mapa y actualiza su cantidad de repeticiones
            if (mapaNumeros.containsKey(num)) {
                mapaNumeros.put(num, mapaNumeros.get(num) + 1);
            } else {
                // Si el número no está en el mapa, lo agrega con una repetición inicial
                mapaNumeros.put(num, 1);
            }
        }
        
        return mapaNumeros;
    }
}
