package Lexer;
/*This file creates staes for every transition for the regular expresions to be used to build NFA and DFAs */

import java.util.*;

public class State {
    private int id;
    private boolean valid_state_check;  //to check if the state is accepting
    private TokenType tok_type; 
    private Map<Character, Set<State>> transitions = new HashMap<>();       //Set<State> -> cause NFA can transition to more than 1 states, while DFA transits to one only    Character-> lexeme char that triggers the transition
    

    public State(int id, boolean valid_state_check, TokenType tok_type) {
        this.id = id;
        this.valid_state_check = valid_state_check;
        this.tok_type = tok_type;
    }

    public TokenType get_TokenType() {
        return tok_type;
    }

    public int get_TokenID() {
         return id; 
    }
    public boolean valid_state_check() {        //check if the state is a valid one to transition too
         return valid_state_check; 
    }

    public void add_StateShift(char lexeme_char, Set<State> states) {    //add the new transitions to the states in FA
        transitions.put(lexeme_char, states); 
    }
    
    public Set<State> get_StateShifts(char lexeme_char) {       //return the key if there or default value if key not present-> hence always a valid state
        return transitions.getOrDefault(lexeme_char, Set.of());     //to avoid Null pointer exception
    }
}