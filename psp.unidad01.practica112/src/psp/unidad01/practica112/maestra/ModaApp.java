package psp.unidad01.practica112.maestra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ModaApp {
    
    private static final String JAR_ESCLAVO = "ModaEsclavaApp.jar"; // Nombre del archivo JAR del proceso esclavo
    
    private static Map<Integer, Integer> mapaNumeros = new HashMap<>(); // Mapa para almacenar los números y sus repeticiones
    
    public static void main(String[] args) throws IOException{
        
        System.out.println("INICIANDO PROGRAMA");
        
        /*
         * Introducimos la ruta del archivo, en caso de que el archivo no se encuentre
         * lanzará un error.
         */
        if (args.length < 1) {
          System.err.print("RUTA NO INTRODUCIDA O NO ACCESIBLE");
          System.exit(0);
        }
        String ruta = args[0]; // Ruta del archivo proporcionada como argumento
        
        /*
         * Definimos la cantidad de procesos 
         */
        int numProcesadores = numeroProcesosAleatorio(); // Número aleatorio de procesos
        
        /*
         * Creamos la factoría de procesos
         */
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", JAR_ESCLAVO);
        
        /*
         * Creamos los procesos esclavos
         */
        List<Process> procesos = new ArrayList<>();
        for (int i = 0; i < numProcesadores; i++) {
            procesos.add(builder.start());
        }
        
        /*
         * Iniciamos los métodos
         */
        List<Integer> numeros = archivoToArray(ruta); // Leemos los números del archivo
        int grupos = gruposDeNumeros(numeros.size(), procesos.size()); // Calculamos la cantidad de elementos por grupo
        int sobrantes = numerosSobrantes(numeros.size(), procesos.size()); // Calculamos la cantidad de elementos adicionales
        
        /*
         * Pasamos los valores a los esclavos
         */
        int vuelta = 0;
        System.out.println("PASANDO VALORES A LOS ESCLAVOS.............");
        for (Process proceso: procesos) {
            BufferedWriter escritor = new BufferedWriter(new OutputStreamWriter(proceso.getOutputStream()));
            PrintWriter printer = new PrintWriter(escritor);
            String entrada = "";
            for (int i = 0; i < grupos; i++) {
                entrada = entrada + numeros.get(vuelta) + "/";
                vuelta++;
            }
            if (sobrantes > 0) {
                entrada = entrada + numeros.get(vuelta) + "/";
                vuelta++;
                sobrantes--;
            }
            printer.println(entrada); // Enviamos los números al proceso esclavo
            printer.flush();
            printer.close();
            System.out.println("GRUPO ENVIADO");
        }
        
        /*
         * Recogemos los valores de los esclavos
         */
        
        System.out.println("RECIBIENDO VALORES DE LOS ESCLAVOS.............");
        for (Process proceso: procesos) {
            BufferedReader br = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            while ((linea = br.readLine()) != null) {
                recibeEsclavo(linea); // Procesamos la línea recibida del esclavo
            }
        }
        
        // Calculamos el valor más repetido y su cantidad de repeticiones
        int valorM = cantidadRepeticiones();
        int claveM = valorMasRepetido();
        
        // Mostramos el resultado
        System.out.println("El valor más repetido es " + claveM + " con un total de " + valorM + " repeticiones.");
        
    }
    
    /**
     * Calcula la cantidad de números sobrantes al dividirlos entre la cantidad de procesos.
     * @param numeros Cantidad total de números
     * @param procesos Cantidad de procesos
     * @return Cantidad de números sobrantes
     */
    private static int numerosSobrantes(int numeros, int procesos) {
        int sobrantes = numeros % procesos;
        return sobrantes;
    }

    /**
     * Calcula la cantidad de grupos de números que se pueden formar al dividirlos entre la cantidad de procesos.
     * @param numeros Cantidad total de números
     * @param procesos Cantidad de procesos
     * @return Cantidad de grupos de números
     */
    private static int gruposDeNumeros(int numeros, int procesos) {
        int grupos = numeros / procesos;
        return grupos;
    }

    /**
     * Genera un número aleatorio de procesos para el programa.
     * @return Número aleatorio de procesos
     */
    public static int numeroProcesosAleatorio() {
        // Generamos un número aleatorio entre 2 y el máximo de procesadores disponibles
        int minProcesos = 2;
        int maxProcesos = Runtime.getRuntime().availableProcessors();
        Random random = new Random();
        int numeroProcesos = random.nextInt(maxProcesos - minProcesos + 1) + minProcesos;
        return numeroProcesos;
    }
    
    /**
     * Lee los números de un archivo y los almacena en una lista.
     * @param ruta Ruta del archivo
     * @return Lista de números leídos
     */
    public static List<Integer> archivoToArray(String ruta) {
        List<Integer> listaContenido = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while((linea = br.readLine()) != null) {
                int numero = Integer.parseInt(linea);
                listaContenido.add(numero);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaContenido;
    }
    
    /**
     * Determina el valor que más veces se repite.
     * @return Valor que más se repite
     */
    public static int valorMasRepetido() {
        int valorM = 0;
        int claveM = 0;
        
        // Iteramos sobre el mapa para encontrar el valor con más repeticiones
        for (Map.Entry<Integer, Integer> entry : mapaNumeros.entrySet()) {
            Integer clave = entry.getKey();
            Integer valor = entry.getValue();
            
            if (valorM == 0 || valor.compareTo(valorM) > 0) {
                valorM = valor;
                claveM = clave;
            }
        }
        
        return claveM;
    }
    
    /**
     * Determina la cantidad de repeticiones del valor que más veces se repite.
     * @return Cantidad de repeticiones del valor que más se repite
     */
    public static int cantidadRepeticiones() {
        int valorM = 0;
        
        // Iteramos sobre el mapa para encontrar la cantidad de repeticiones del valor con más repeticiones
        for (Map.Entry<Integer, Integer> entry : mapaNumeros.entrySet()) {
            Integer valor = entry.getValue();
            
            if (valorM == 0 || valor.compareTo(valorM) > 0) {
                valorM = valor;
            }
        }
        
        return valorM;
    }
    
    /**
     * Recibe los valores de los esclavos y los almacena en un mapa.
     * @param linea Línea recibida del esclavo
     */
    public static void recibeEsclavo(String linea) {     
        if (!linea.isEmpty()) {
            for (String distintosValores : linea.split(" ")) {
                String[] partes = distintosValores.split("=");
                
                int clave = Integer.parseInt(partes[0]);
                int valor = Integer.parseInt(partes[1]);
                
                if (mapaNumeros.containsKey(clave)) {
                    int valorActual = mapaNumeros.get(clave);
                    mapaNumeros.put(clave, valorActual + valor);
                } else {
                    mapaNumeros.put(clave, valor);
                }
            }
        }
    }
    
}
