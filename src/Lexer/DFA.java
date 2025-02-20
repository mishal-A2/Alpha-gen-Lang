package Lexer;
/* This file builds upon the nfa generated in NFA.java and converts it to dfa dealing with the null transitions too.
    Utilizing files: NFA.java
 
 */

import java.util.*;

public class DFA {
    private Map<Set<State>, Map<Character, Set<State>>> transitionTable = new HashMap<>();
    private Set<State> startState;
    private Set<State> acceptingStates;

    public DFA(Set<State> startState) {
        this.startState = startState;
        this.acceptingStates = new HashSet<>();
        bulidDFA_from_NFA();
    }

    public Set<State> getStartState() {
        return startState;
    }

    public Map<Set<State>, Map<Character, Set<State>>> getTransitionTable() {
        return transitionTable;
    }

    public Set<State> getAcceptingStates() {
        return acceptingStates;
    }

    //using subset notation
    private void bulidDFA_from_NFA() {
        Queue<Set<State>> fringe_queue = new LinkedList<>();
        Set<Set<State>> visited = new HashSet<>();

        //epsilon closure used first
        Set<State> startClosure = epsilonClosure(startState);
        fringe_queue.add(startClosure);
        visited.add(startClosure);

        while (!fringe_queue.isEmpty()) {
            Set<State> currentStates = fringe_queue.poll();

            
            for (State state : currentStates) {
                if (state.valid_state_check()) {
                    acceptingStates.add(state);
                }
            }

            // Process each input symbol (ASCII range 32-127) -> special char, letters, nums
            for (char c = 32; c < 127; c++) {
                Set<State> nextStates = new HashSet<>();

               
                for (State state : currentStates) {
                    nextStates.addAll(state.get_StateShifts(c));
                }

                
                Set<State> nextClosure = epsilonClosure(nextStates);

                if (!nextClosure.isEmpty()) {
                    
                    transitionTable.computeIfAbsent(currentStates, k -> new HashMap<>())
                                  .put(c, nextClosure);

                    // If the next state set not visited, add it to the fringe_queue
                    if (!visited.contains(nextClosure)) {
                        visited.add(nextClosure);
                        fringe_queue.add(nextClosure);
                    }
                }
            }
        }
    }

    public Set<State> transition(Set<State> currentStates, char input) {
        Set<State> nextStates = new HashSet<>();
        if (transitionTable.containsKey(currentStates)) {
            Map<Character, Set<State>> transitions = transitionTable.get(currentStates);
            if (transitions.containsKey(input)) {
                nextStates.addAll(transitions.get(input));
            }
        }
        return nextStates;
    }
    

    //calc epsilon closure for all accepting states
    private Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Stack<State> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            State current = stack.pop();
            for (State nextState : current.get_StateShifts('\0')) {    // '\0' represents epsilon
                if (!closure.contains(nextState)) {
                    closure.add(nextState);
                    stack.push(nextState);
                }
            }
        }

        return closure;
    }

   
    public void display_transTable(String input) {
        System.out.println("Transition Table:");
        System.out.println("Input used for transition table:");
        System.out.println(input);
        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-10s %-10s %-20s\n", "Current", "Symbol", "Next States");
        System.out.println("--------------------------------------------------------------");

        for (Map.Entry<Set<State>, Map<Character, Set<State>>> entry : transitionTable.entrySet()) {
            Set<State> currentStates = entry.getKey();
            Map<Character, Set<State>> transitions = entry.getValue();

            for (Map.Entry<Character, Set<State>> transition : transitions.entrySet()) {
                char symbol = transition.getKey();
                Set<State> nextStates = transition.getValue();

                System.out.printf("%-10s %-10s %-20s\n", 
                    state_2_str(currentStates), "'" + symbol + "'", state_2_str(nextStates), "'");
            }
        }

        System.out.println("--------------------------------------------------------------");
        System.out.println("Total states: " + transitionTable.size());
    }

    // helper to convert states to strings
    private String state_2_str(Set<State> states) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (State state : states) {
            sb.append("State ").append(state.get_TokenID()).append(", ");
        }
        if (!states.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }
}