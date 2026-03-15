package problem2;

import java.io.*;
import java.util.*;

public class Main {
    
    public static void main(String[] args) {
    	
    	//prompts user for file name to read from
        Scanner scanner = new Scanner(System.in);     
        System.out.println("  Regex to NFA Converter");       
        System.out.print("Enter the input file name: ");
        String filename = scanner.nextLine();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String regex = reader.readLine();
            reader.close();
            
            if (regex == null || regex.trim().isEmpty()) {
                System.out.println("Error: Empty regex in file");
                scanner.close();
                return;
            }
            
            // Remove all whitespace
            regex = regex.replaceAll("\\s+", "");
            
            System.out.println("Input Regular Expression: " + regex + "\n");
            
            // Convert regex to NFA
            RegexToNFA converter = new RegexToNFA(regex);
            NFA nfa = converter.convert();
            
            // Test strings
            System.out.println("  Testing Strings Against NFA");
            
            //loop until user ends program
            while (true) {
                System.out.print("Enter string to test (or 'quit' to exit): ");
                String test = scanner.nextLine();
                
                if (test.equalsIgnoreCase("quit")) {
                    System.out.println("\nProgram ended.");
                    break;
                }
                
                System.out.println();
                boolean result = nfa.accepts(test);
                System.out.println(result);
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: File '" + filename + "' not found");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}