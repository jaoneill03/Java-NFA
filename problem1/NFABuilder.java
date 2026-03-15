package problem1;

import java.util.List;

//helper class to build the NFA
public class NFABuilder {
	private static final char LAMBDA = '\0';
	
	//creates NFA from a list of strings
	public static NFA buildFromStrings(List<String> strings) {
		NFA nfa = new NFA();
		
		
		int startState = nfa.addState(false);
		nfa.setStartState(startState);
		
		//loops through the list of strings and creates a separate path for each string
		for(String str : strings) {
			//creates new accept state if string is empty
			if(str.isEmpty()) {
				int acceptState = nfa.addState(true);
				nfa.addTransition(startState, LAMBDA, acceptState);
				
			}
			
			//creates the path for nonempty strings
			int firstStateOfPath = nfa.addState(false);
			nfa.addTransition(startState, LAMBDA, firstStateOfPath);
			int currentState = firstStateOfPath;
			
			//loops through the characters for that string
			for(int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				boolean isLastChar = (i == str.length() -1);
				int nextState = nfa.addState(isLastChar);
				nfa.addTransition(currentState, c, nextState);
				currentState = nextState;
			}
			
		}
		return nfa;
	}
}
