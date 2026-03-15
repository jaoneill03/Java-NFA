package problem1;

import java.io.*;
import java.util.*;

public class Main {
    
    public static void main(String[] args) {
    	
    	//gets the file name from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the input file name: ");
        String filename = scanner.nextLine();
        
        
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            
            // Read first line and stores it as the alphabet for the language
            String alphabetLine = reader.readLine();
            if (alphabetLine == null) {
                System.out.println("Error: File is empty");
                reader.close();
                return;
            }
            
            //divides the first line into just the letters for the language and outputs it
            String[] alphabet = alphabetLine.split(",");
            System.out.println("Alphabet: " + Arrays.toString(alphabet));
            
            //reads the strings in after the first line 
            List<String> language = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    language.add(line);
                }
            }
            reader.close();
            
            //prints out the language strings
            System.out.println("\nLanguage L contains " + language.size() + " strings:");
            for (String str : language) {
                System.out.println("  '" + str + "'");
            }
            
            // Builds NFA
            NFA nfa = NFABuilder.buildFromStrings(language);
            nfa.printNFA();
            
            
            // Test strings from the users input to see if they are accepted or rejected
            //also asks user if they would like to end the program
            while (true) {
                System.out.print("\nEnter a string to test (or quit to exit): ");
                String testString = scanner.nextLine();
                
                if (testString.equalsIgnoreCase("quit")) {
                    System.out.println("End program");
                    break;
                }
                
                boolean result = nfa.accepts(testString);
                System.out.println("Result: " + (result ? "accept" : "reject"));
                if(result = true) {
                	nfa.printNFA();
                }
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: File '" + filename + "' not found.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}