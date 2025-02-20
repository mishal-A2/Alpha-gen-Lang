package Lexer;

/* Identifies and creates all the NFA's on the basis of the Regular expressions defined in the 
   Utilizing the files: State.java
*/

import java.util.*;

public class NFA {
    private State startin_state;
    private State endin_state;

    public NFA(State startin_state, State endin_state) {
        this.startin_state = startin_state;
        this.endin_state = endin_state;
    }

    public State get_StartState() {
        return startin_state;
    }

    public State get_EndState() {
        return endin_state;
    }

    //Exponent/ power NFA -> 1.23e5/ 56e-3
    public static NFA createExponentNFA() {
        State s0 = new State(0, false, null); 
        State s1 = new State(1, false, null); //int part
        State s2 = new State(2, false, null); //the decimal point -> "."
        State s3 = new State(3, false, null); //float part
        State s4 = new State(4, false, null); // 'e' | 'E'
        State s5 = new State(5, false, null); // power sign
        State s6 = new State(6, true, TokenType.EXPONENT); 

        //checking for int
        for (char state_input = '0'; state_input <= '9'; state_input++) {
            s0.add_StateShift(state_input, Set.of(s1));
            s1.add_StateShift(state_input, Set.of(s1));
        }

        // checking for decimal point
        s1.add_StateShift('.', Set.of(s2));

        // checking for float after "."
        for (char state_input = '0'; state_input <= '9'; state_input++) {
            s2.add_StateShift(state_input, Set.of(s3));
            s3.add_StateShift(state_input, Set.of(s3));
        }

        // 'e' | 'E' after int, "." , float
        s1.add_StateShift('e', Set.of(s4)); 
        s3.add_StateShift('e', Set.of(s4)); 
        s1.add_StateShift('E', Set.of(s4)); 
        s3.add_StateShift('E', Set.of(s4)); 

        // checkin power sign
        s4.add_StateShift('+', Set.of(s5));
        s4.add_StateShift('-', Set.of(s5));

        // checking for power value
        for (char state_input = '0'; state_input <= '9'; state_input++) {
            s5.add_StateShift(state_input, Set.of(s6));
            s6.add_StateShift(state_input, Set.of(s6));
        }

        return new NFA(s0, s6);
    }

    //Float NFA-> correct to upto 5dp
    public static NFA createFloatNFA() {
        State s0 = new State(8, false, null); 
        State s1 = new State(9, false, null); // int part
        State s2 = new State(10, false, null); // float part
        //s3->s6 for 5dp 
        State s3 = new State(11, true, TokenType.DECIMAL); 
        State s4 = new State(12, true, TokenType.DECIMAL);
        State s5 = new State(13, true, TokenType.DECIMAL);
        State s6 = new State(14, true, TokenType.DECIMAL);
        State s7 = new State(15, true, TokenType.DECIMAL);
        
        // checking for int 
        for (char state_input = '0'; state_input <= '9'; state_input++) {
            s0.add_StateShift(state_input, Set.of(s1));
            s1.add_StateShift(state_input, Set.of(s1));
        }

        // checking for dp
        s1.add_StateShift('.', Set.of(s2));

        // correct to 5dp
        for (char state_input = '0'; state_input <= '9'; state_input++) {
            s2.add_StateShift(state_input, Set.of(s3));
            s3.add_StateShift(state_input, Set.of(s4));
            s4.add_StateShift(state_input, Set.of(s5));
            s5.add_StateShift(state_input, Set.of(s6));
            s6.add_StateShift(state_input, Set.of(s7));
        }

        return new NFA(s0, s7);
    }

    // Integers NFA -> [0-9]+
    public static NFA createIntNFA() {
        State start = new State(13, false, null);
        State end = new State(14, true, TokenType.CONSTANTS);

        for (char state_input = '0'; state_input <= '9'; state_input++) {
            start.add_StateShift(state_input, Set.of(end));
            end.add_StateShift(state_input, Set.of(end)); 
        }

        return new NFA(start, end);
    }

    // Arithematic Operators NFA -> [+|-|*|%|/]
    public static NFA createOperatorNFA() {
        State start = new State(15, false, null);
        State end = new State(16, true, TokenType.OPERATOR);

        char[] operatioins = {'+', '-', '*', '/', '=', '%'};
        for (char oper : operatioins) {
            start.add_StateShift(oper, Set.of(end));
        }

        return new NFA(start, end);
    }

    // PunctuationNFA -> [ (|)|{|}|,|;|: ]
    public static NFA createPunctuationNFA() {
        State start = new State(17, false, null);
        State end = new State(18, true, TokenType.PUNCTUATION);

        char[] punctuations = {'(', ')', '{', '}', ',', ';', ':'};
        for (char punc : punctuations) {
            start.add_StateShift(punc, Set.of(end));
        }

        return new NFA(start, end);
    }

    //Keyword NFA -> [a-zA-Z]+
    public static NFA createKeywordNFA(String keywrd, TokenType tok_type) {
        State startin_state = new State(0, false, null);
        State curr_state = startin_state;
    
        for (int i = 0; i < keywrd.length(); i++) {
            State next_state = new State(i + 1, i == keywrd.length() - 1, i == keywrd.length() - 1 ? tok_type : null);  //assigns tok only if its last char or else null
            curr_state.add_StateShift(keywrd.charAt(i), Set.of(next_state));
            curr_state = next_state;
        }
    
        return new NFA(startin_state, curr_state);
    }

    // Comments NFA -> [/* not * */]
    public static NFA createCommentNFA() {
        State s0 = new State(24, false, null); 
        State s1 = new State(25, false, null); // startin '/'
        State s2 = new State(26, false, null); // startin '*'
        State s3 = new State(27, false, null); // comment content
        State s4 = new State(28, false, null); // ending * before /
        State s5 = new State(29, true, TokenType.COMMENT); 

        s0.add_StateShift('/', Set.of(s1));
        s1.add_StateShift('*', Set.of(s2));

        //comment content [0-128]-> ascii 
        for (char state_input = 0; state_input < 128; state_input++) {
            if (state_input != '*') {
                s2.add_StateShift(state_input, Set.of(s2));
                s3.add_StateShift(state_input, Set.of(s2));
            }
        }
       
        s2.add_StateShift('*', Set.of(s4));
        s4.add_StateShift('/', Set.of(s5)); 

        return new NFA(s0, s5);
    }

    // Global NFA-> [$a-zA-Z]
    public static NFA createGlobalNFA() {
        State s0 = new State(30, false, null); 
        State s1 = new State(31, true, TokenType.GLOBAL); 

        s0.add_StateShift('$', Set.of(s1));     // $ to indicate gloabl like in bash
        for (char state_input = 'a'; state_input <= 'z'; state_input++) {
            s1.add_StateShift(state_input, Set.of(s1));
        }
        for (char state_input = 'A'; state_input <= 'Z'; state_input++) {
            s1.add_StateShift(state_input, Set.of(s1));
        }

        return new NFA(s0, s1);
    }

    // Local NFA -> any var
    public static NFA createLocalNFA() {
        State s0 = new State(32, false, null);
        State s1 = new State(33, true, TokenType.LOCAL); 

        for (char state_input = 'a'; state_input <= 'z'; state_input++) {
            s0.add_StateShift(state_input, Set.of(s1));
        }
        for (char state_input = 'A'; state_input <= 'Z'; state_input++) {
            s0.add_StateShift(state_input, Set.of(s1));
        }

        return new NFA(s0, s1);
    }
    
    //Identifier NFA ->
    public static NFA createIdentifierNFA() {
        State start = new State(48, false, null);
        State end = new State(49, true, TokenType.IDENTIFIER);

        //for lowercase
        for (char state_input = 'a'; state_input <= 'z'; state_input++) {
            start.add_StateShift(state_input, Set.of(end));
            end.add_StateShift(state_input, Set.of(end));
        }
        //for uppercase-> but generates lexical error later
        for (char state_input = 'A'; state_input <= 'Z'; state_input++) {
            start.add_StateShift(state_input, Set.of(end));
            end.add_StateShift(state_input, Set.of(end));
        }

        return new NFA(start, end);
    }
    
    // Literal NFA -> ["a-zA-Z"]
    public static NFA createStringNFA() {
        State s0 = new State(50, false, null);  
        State s1 = new State(51, false, null); 
        State s2 = new State(52, true, TokenType.STRING); 

        //first "
        s0.add_StateShift('"', Set.of(s1));

        
        for (char state_input = 32; state_input < 127; state_input++) {
            if (state_input != '"') {
                s1.add_StateShift(state_input, Set.of(s1));
            }
        }
       
        // closing "
        s1.add_StateShift('"', Set.of(s2));

        return new NFA(s0, s2);
    }
}
