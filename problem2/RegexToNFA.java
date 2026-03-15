package problem2;

import java.util.*;

public class RegexToNFA {
    private String regex;
    private NFA nfa;
    private int counter;
    
    private static final char LAMBDA = '\0';  
    
    // Fragment  represents a partial NFA with start and accept states
    private static class NFAFragment {
        int startState;
        int acceptState;
        String expression; // Track what this fragment is
        
        NFAFragment(int start, int accept, String expr) {
            this.startState = start;
            this.acceptState = accept;
            this.expression = expr;
        }
    }
    
    //constructor to store regex as a string, create empty NFA, and keep track of steps with a counter
    public RegexToNFA(String regex) {
        this.regex = regex;
        this.nfa = new NFA();
        this.counter = 1;
    }
    
    
    //method for converting the entire regex to an NFA
    public NFA convert() {
        System.out.println("Converting Regex: " + regex);
        
        NFAFragment result = buildNFA(regex, 0);
        
        if (result == null) {
            System.out.println("Error: Invalid regex");
            return nfa;
        }
        
        nfa.setStartState(result.startState);
        nfa.markAsAccept(result.acceptState);
        
        System.out.println("\nResulting NFA");
        nfa.printNFA();
        
        return nfa;
    }
    
    //
    private NFAFragment buildNFA(String expr, int depth) {
    	
        String indent = getIndent(depth);//tracks recursion level
        System.out.println(indent + "Level " + depth);
        System.out.println(indent + "Processing: " + expr);
        Stack<NFAFragment> fragmentStack = new Stack<>();//holds built NFA fragments
        Stack<Character> operatorStack = new Stack<>();//holds operators
        
        //processes each character
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            
            //handles parenthesizes
            if (c == '(') {
                int depth_paren = 1;
                int j = i + 1;
                while (j < expr.length() && depth_paren > 0) {
                    if (expr.charAt(j) == '(') depth_paren++;
                    if (expr.charAt(j) == ')') depth_paren--;
                    j++;
                }
                
                
                String subExpr = expr.substring(i + 1, j - 1);
                System.out.println(indent + "\n[Step " + (counter++) + "] Found parenthesized expression: (" + subExpr + ")");
                
                // Recursively builds NFA for parenthesized expression
                NFAFragment subFragment = buildNFA(subExpr, depth + 1);
                
                // Checks for Kleene star after parenthesis
                if (j < expr.length() && expr.charAt(j) == '*') {
                    System.out.println(indent + "[Step " + (counter++) + "] Applying Kleene star to: (" + subExpr + ")");
                    System.out.println(indent + "  Building r* where r = " + subExpr);
                    subFragment = buildKleeneStar(subFragment, "(" + subExpr + ")*", indent);
                    j++;
                }
                
                fragmentStack.push(subFragment);
                i = j;
                
              //handles union operator
            } else if (c == '+') {
                System.out.println(indent + "\n[Step " + (counter++) + "] Found UNION operator (+)");
                operatorStack.push('+');
                i++;
                
                //handles characters or digits in the string
            } else if (Character.isLetterOrDigit(c)) {
                // Build single character or sequence
                StringBuilder sequence = new StringBuilder();
                while (i < expr.length() && Character.isLetterOrDigit(expr.charAt(i))) {
                    sequence.append(expr.charAt(i));
                    i++;
                }
                
                String seq = sequence.toString();
                System.out.println(indent + "\n[Step " + (counter++) + "] Processing sequence: " + seq);
                NFAFragment seqFragment = buildSequence(seq, indent);
                
                // Check for Kleene star after
                if (i < expr.length() && expr.charAt(i) == '*') {
                    System.out.println(indent + "[Step " + (counter++) + "] Applying Kleene star to: " + seq);
                    System.out.println(indent + "  Building r* where r = " + seq);
                    seqFragment = buildKleeneStar(seqFragment, seq + "*", indent);
                    i++;
                }
                
                fragmentStack.push(seqFragment);
                
            } else {
                i++; 
            }
        }
        
        // Processes the union operator
        while (!operatorStack.isEmpty()) {
            char op = operatorStack.pop();
            
            if (op == '+') {
                if (fragmentStack.size() >= 2) {
                    NFAFragment r2 = fragmentStack.pop();
                    NFAFragment r1 = fragmentStack.pop();
                    
                    System.out.println(indent + "\n[Step " + (counter++) + "] Building UNION");
                    System.out.println(indent + "  r1 = " + r1.expression);
                    System.out.println(indent + "  r2 = " + r2.expression);
                    System.out.println(indent + "  Result: r1 + r2");
                    
                    NFAFragment unionFragment = buildUnion(r1, r2, indent);
                    fragmentStack.push(unionFragment);
                }
            }
        }
        
        // handles concatenation if multiple fragments left
        if (fragmentStack.size() > 1) {
            System.out.println(indent + "\n[Step " + (counter++) + "] Building CONCATENATION of remaining fragments");
            
            List<NFAFragment> fragments = new ArrayList<>();
            while (!fragmentStack.isEmpty()) {
                fragments.add(0, fragmentStack.pop()); // reverse order
            }
            
            NFAFragment result = fragments.get(0);
            for (int k = 1; k < fragments.size(); k++) {
                System.out.println(indent + "  r1 = " + result.expression);
                System.out.println(indent + "  r2 = " + fragments.get(k).expression);
                System.out.println(indent + "  Result: r1r2 (concatenation)");
                
                result = buildConcat(result, fragments.get(k), indent);
            }
            
            return result;
        }
        
        return fragmentStack.isEmpty() ? null : fragmentStack.pop();
    }
    
    
    
    private NFAFragment buildSequence(String seq, String indent) {
    	//for one character
        if (seq.length() == 1) {
            int start = nfa.addState(false);
            int accept = nfa.addState(false);
            nfa.addTransition(start, seq.charAt(0), accept);
            
            System.out.println(indent + "  Created: " + start + " --" + seq.charAt(0) + "--> " + accept);
            
            return new NFAFragment(start, accept, seq);
        }
        
        // for multiple characters by concatenate them
        System.out.println(indent + "  Concatenating characters: " + seq);
        NFAFragment result = null;
        
        for (int i = 0; i < seq.length(); i++) {
            char c = seq.charAt(i);
            int start = nfa.addState(false);
            int accept = nfa.addState(false);
            nfa.addTransition(start, c, accept);
            
            NFAFragment charFrag = new NFAFragment(start, accept, String.valueOf(c));
            
            if (result == null) {
                result = charFrag;
            } else {
                System.out.println(indent + "    Linking " + result.acceptState + " to " + charFrag.startState);
                nfa.addTransition(result.acceptState, LAMBDA, charFrag.startState);
                result = new NFAFragment(result.startState, charFrag.acceptState, result.expression + c);
            }
        }
        
        return result;
    }
    
    //builds union for r1 + r2
    private NFAFragment buildUnion(NFAFragment r1, NFAFragment r2, String indent) {
        int newStart = nfa.addState(false);
        int newAccept = nfa.addState(false);
        
        System.out.println(indent + "  Structure:");
        System.out.println(indent + "    New start (" + newStart + ") --λ--> r1.start (" + r1.startState + ")");
        System.out.println(indent + "    New start (" + newStart + ") --λ--> r2.start (" + r2.startState + ")");
        System.out.println(indent + "    r1.accept (" + r1.acceptState + ") --λ--> New accept (" + newAccept + ")");
        System.out.println(indent + "    r2.accept (" + r2.acceptState + ") --λ--> New accept (" + newAccept + ")");
        
        nfa.addTransition(newStart, LAMBDA, r1.startState);
        nfa.addTransition(newStart, LAMBDA, r2.startState);
        nfa.addTransition(r1.acceptState, LAMBDA, newAccept);
        nfa.addTransition(r2.acceptState, LAMBDA, newAccept);
        
        nfa.printNFA();
        
        return new NFAFragment(newStart, newAccept, "(" + r1.expression + ")+(" + r2.expression + ")");
    }
    
    //builds concatenation for r1r2
    private NFAFragment buildConcat(NFAFragment r1, NFAFragment r2, String indent) {
        System.out.println(indent + "  Structure:");
        System.out.println(indent + "    r1.accept (" + r1.acceptState + ") --λ--> r2.start (" + r2.startState + ")");
        
        nfa.addTransition(r1.acceptState, LAMBDA, r2.startState);
        
        nfa.printNFA();
        
        return new NFAFragment(r1.startState, r2.acceptState, r1.expression + r2.expression);
    }
    
    // builds kleene star for r
    private NFAFragment buildKleeneStar(NFAFragment r, String expr, String indent) {
        int newStart = nfa.addState(false);
        int newAccept = nfa.addState(false);
        
        System.out.println(indent + "  Structure:");
        System.out.println(indent + "    New start (" + newStart + ") --λ--> r.start (" + r.startState + ") [enter loop]");
        System.out.println(indent + "    r.accept (" + r.acceptState + ") --λ--> r.start (" + r.startState + ") [repeat]");
        System.out.println(indent + "    r.accept (" + r.acceptState + ") --λ--> New accept (" + newAccept + ") [exit]");
        System.out.println(indent + "    New start (" + newStart + ") --λ--> New accept (" + newAccept + ") [skip]");
        
        nfa.addTransition(newStart, LAMBDA, r.startState);
        nfa.addTransition(r.acceptState, LAMBDA, r.startState);
        nfa.addTransition(r.acceptState, LAMBDA, newAccept);
        nfa.addTransition(newStart, LAMBDA, newAccept);
        
        nfa.printNFA();
        
        return new NFAFragment(newStart, newAccept, expr);
    }
    
    private String getIndent(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
}