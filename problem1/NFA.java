package problem1;

import java.util.ArrayList;
import java.util.List;

public class NFA {
	private int numStates;
	private int startState;
	private boolean[] isAcceptState;//array of booleans for if a state is an accept state or not
	private List<Transition> transitions;//list of transitions
	
	private static final char LAMBDA = '\0';//used to represent lambda
	private static final int MAX_STATES = 1000;
	
	//makes a class for a transition object that holds the previous transition
	//symbol and next transition
	private static class Transition{
		int from;
		int to;
		char symbol;
		
		Transition(int from, char symbol, int to){
			this.from = from;
			this.symbol = symbol;
			this.to = to;
			
		}
	}
	
	//constructor for NFA object that counts the number of states, arraylist of transitions, and 
	//array of accept states
	public NFA() {
		this.numStates = 0;
		this.isAcceptState = new boolean[MAX_STATES];
		this.transitions = new ArrayList<>();		
	}
	
	//creates a new state 
	public int addState(boolean isAccept) {
        int stateId = numStates;
        numStates++;
        isAcceptState[stateId] = isAccept;
        return stateId;
    }
	
	//says which state is the start state
	public void setStartState(int state) {
        this.startState = state;
    }
	
	//adds a transition to the NFA 
	public void addTransition(int from, char symbol, int to) {
        transitions.add(new Transition(from, symbol, to));
    }
    
	//takes a state and marks it as an accept state(true)
	//for isAcceptState array
    public void markAsAccept(int state) {
        isAcceptState[state] = true;
    }
	
    //finds what states are reachable from the given state and symbol
    private int[] getTransitions(int from, char symbol) {
        int[] temp = new int[numStates];
        int count = 0;
        
        for (Transition t : transitions) {
            if (t.from == from && t.symbol == symbol) {
                temp[count++] = t.to;
            }
        }
        
        int[] result = new int[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }
    
    
    
    // computes what states are reachable using lambda transition for a single state
    private boolean[] lambdaTransitionArray(int state) {
        boolean[] lambdaTransitions = new boolean[numStates];
        int[] stack = new int[numStates];
        int stackSize = 0;
        
        stack[stackSize++] = state;
        
        while (stackSize > 0) {
            int current = stack[--stackSize];
            
            if (lambdaTransitions[current]) {
                continue;
            }
            
            lambdaTransitions[current] = true;
            
            int[] lambdaDests = getTransitions(current, LAMBDA);
            for (int dest : lambdaDests) {
                if (!lambdaTransitions[dest]) {
                    stack[stackSize++] = dest;
                }
            }
        }
        
        return lambdaTransitions;
    }
    
    // computes what states are reachable using lambda transition for multiple states
    private boolean[] lambdaTransitionArray(boolean[] states) {
        boolean[] lambdaTransitions = new boolean[numStates];
        
        for (int i = 0; i < numStates; i++) {
            if (states[i]) {
                boolean[] stateClosure = lambdaTransitionArray(i);
                for (int j = 0; j < numStates; j++) {
                    if (stateClosure[j]) {
                    	lambdaTransitions[j] = true;
                    }
                }
            }
        }
        
        return lambdaTransitions;
    }
    
    
    //computes if a string is accepted in the consructed NFA
    public boolean accepts(String input) {
        boolean[] currentStates = new boolean[numStates];//tracks current state
        currentStates[startState] = true;//marks start state as the active state
        currentStates = lambdaTransitionArray(currentStates);  // Take free lambda transitions from start
        
        System.out.print("Path for " + input + ": State " + startState);
        
        // prints which states are reachable from beginning lambda transitions
        System.out.print(" --lambda--> {");
        boolean first = true;
        for (int s = 0; s < numStates; s++) {
            if (currentStates[s]) {
                if (!first) { 
                	System.out.print(", ");
                }
                
                first = false;
                if(s != 0) {
                    System.out.print(s);
                }else {
                	first = true;
                }
            }
        }
        System.out.print("})");
        
        
        
        // Process each character input from string
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            boolean[] nextStates = new boolean[numStates];
            
            // For each current state it gets transitions on the current character 
            for (int state = 0; state < numStates; state++) {
                if (currentStates[state]) {
                    int[] destinations = getTransitions(state, c);
                    for (int dest : destinations) {
                        nextStates[dest] = true;
                    }
                }
            }
            
            // Takes free lambda transitions
            currentStates = lambdaTransitionArray(nextStates);
            
            // Checks if any state is reachable
            boolean hasStates = false;
            for (int s = 0; s < numStates; s++) {
                if (currentStates[s]) {
                    hasStates = true;
                    break;
                }
            }
            
            if (!hasStates) {
                System.out.println();
                System.out.println("REJECT: No valid transitions for '" + c + "'");
                return false;
            }
            
            // Print transition and resulting states after lambda transitions
            System.out.print(" --" + c + "--> {");
            first = true;
            for (int s = 0; s < numStates; s++) {
                if (currentStates[s]) {
                    if (!first) System.out.print(", ");
                    System.out.print(s);
                    first = false;
                }
            }
            System.out.print("}");
        }
        
        // Checks for accept states
        int acceptState = -1;
        for (int state = 0; state < numStates; state++) {
            if (currentStates[state] && isAcceptState[state]) {
                acceptState = state;
                break;
            }
        }
        
        System.out.println();
        if (acceptState != -1) {
            System.out.println("ACCEPT (reached accept state " + acceptState + ")");
            return true;
        } else {
            System.out.println("REJECT: No accept state reached");
            return false;
        }
    }
    
    
    //prints out the NFA
    public void printNFA() {
        System.out.println("    NFA    ");
        System.out.print("States: ");
        for (int i = 0; i < numStates; i++) {
            System.out.print(i + " ");
        }
        
        System.out.println("\nStart State: " + startState);
        
        System.out.print("Accept States: ");
        for (int i = 0; i < numStates; i++) {
            if (isAcceptState[i]) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
        
        System.out.println("\nTransitions:");
        for (Transition t : transitions) {
            String symbol = (t.symbol == LAMBDA) ? "lambda" : String.valueOf(t.symbol);
            System.out.println("  " + t.from + " --" + symbol + "--> " + t.to);
        }
    }
}
