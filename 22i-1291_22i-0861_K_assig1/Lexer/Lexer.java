package Lexer;
/* this file uses the defined dfa machine to analyze the input code and break them into tokens defined in TokenType.java file by runnig them on the dfa.
    Utilizing the files: DFA.java, TokenType.java
 */
import java.util.*;

public class Lexer {
    private String input_test;
    private int pos = 0;
    private int readin_line = 1;    // to keep track of error
    public DFA dfa_machine;

    public Lexer(String input_test, Map<String, TokenType> keywrds) {
        this.input_test = input_test;
        this.dfa_machine = buildDFAFromRegex(keywrds); 
    }

    public DFA buildDFAFromRegex(Map<String, TokenType> keywrds) {
        // Create NFAs from with specified language keywords
        NFA exponentNFA = NFA.createExponentNFA();
        NFA floatNFA = NFA.createFloatNFA();
        NFA intNFA = NFA.createIntNFA();
        NFA operatorNFA = NFA.createOperatorNFA();
        NFA punctuationNFA = NFA.createPunctuationNFA();
        NFA returnNFA = NFA.createKeywordNFA("yeet", TokenType.KEYWORD);
        NFA commentNFA = NFA.createCommentNFA();
        NFA globalNFA = NFA.createGlobalNFA();
        NFA localNFA = NFA.createLocalNFA();
        NFA digiNFA = NFA.createKeywordNFA("digi", TokenType.KEYWORD);
        NFA pointdigiNFA = NFA.createKeywordNFA("pointdigi", TokenType.DECIMAL);
        NFA identifierNFA = NFA.createIdentifierNFA();
        NFA stringNFA = NFA.createStringNFA(); 

        // combine starting states by the precedence order/ priority
        Set<State> startStates = new HashSet<>();
        startStates.add(exponentNFA.get_StartState());
        startStates.add(floatNFA.get_StartState());
        startStates.add(intNFA.get_StartState());
        startStates.add(operatorNFA.get_StartState());
        startStates.add(punctuationNFA.get_StartState());
        startStates.add(returnNFA.get_StartState());
        startStates.add(commentNFA.get_StartState());
        startStates.add(globalNFA.get_StartState());
        startStates.add(localNFA.get_StartState());
        startStates.add(digiNFA.get_StartState());
        startStates.add(pointdigiNFA.get_StartState());
        startStates.add(identifierNFA.get_StartState());
        startStates.add(stringNFA.get_StartState()); 

        //nfa creation for dynamic keywords
        for (Map.Entry<String, TokenType> entry : keywrds.entrySet()) {
            NFA keywordNFA = NFA.createKeywordNFA(entry.getKey(), entry.getValue());
            startStates.add(keywordNFA.get_StartState());
        }

        return new DFA(startStates);
    }

    public Token nextToken() {
        // Skip whitespace and updating lines to read muti-line comments
        while (pos < input_test.length()) {
            char curr_char = input_test.charAt(pos);
            
            if (Character.isWhitespace(curr_char)) {
                if (curr_char == '\n') {
                    readin_line++;
                }
                pos++;
            } else if (curr_char == '/' && pos + 1 < input_test.length() && input_test.charAt(pos + 1) == '*') {
                break;  //on seeing comment stop skipping
            } else {
                break;
            }
        }

        if (pos >= input_test.length()) {
            return new Token(TokenType.END_OF_FILE, "");    //reached the end of input
        }

        int startin_pos = pos;
        Set<State> curr_states = dfa_machine.getStartState();
        TokenType tok_type = null;

        while (pos < input_test.length()) {
            char curr_char = input_test.charAt(pos);
            if (curr_char == '/' && pos + 1 < input_test.length() && input_test.charAt(pos + 1) == '*') {
                int commentStart = pos;  
                pos += 2;  // Skip the "/*"
                while (pos + 1 < input_test.length() && !(input_test.charAt(pos) == '*' && input_test.charAt(pos + 1) == '/')) {
                    if (input_test.charAt(pos) == '\n') {
                        readin_line++; 
                    }
                    pos++;
                }
                if (pos + 1 < input_test.length()) {
                    pos += 2;  // Skip the closing "*/"
                }
                String full_cmt_text = input_test.substring(commentStart, pos); 
                return new Token(TokenType.COMMENT, full_cmt_text);
            }
            
            curr_states = dfa_machine.transition(curr_states, curr_char);

            if (curr_states.isEmpty()) {
                if (tok_type != null) {
                    String value = input_test.substring(startin_pos, pos);
                    if (tok_type == TokenType.DECIMAL) {
                        value = roundOffDecimal(value);
                    }
                    // check if identifier
                    if (tok_type == TokenType.IDENTIFIER && !value.matches("[a-z]+")) {
                        throw new RuntimeException("Invalid identifier: '" + value + "' at readin_line " + readin_line + ". Identifiers must be lowercase only.");
                    }
                    return new Token(tok_type, value);
                } else {
                    //invalid char
                    pos++;
                    startin_pos = pos;
                    curr_states = dfa_machine.getStartState();
                    continue;
                }
            }

            pos++;

            
            if (is_valid_state(curr_states)) {
                tok_type = getTokenType(curr_states);
            }
        }

        if (tok_type != null) {
            String value = input_test.substring(startin_pos, pos);
            if (tok_type == TokenType.DECIMAL) {
                value = roundOffDecimal(value);
            }
            // check identifier -> Error handling
            if (tok_type == TokenType.IDENTIFIER && !value.matches("[a-z]+")) {
                throw new RuntimeException("Invalid identifier: '" + value + "' at readin_line " + readin_line + ". Identifiers must be lowercase only.");
            }
            return new Token(tok_type, value);
        } else {
            throw new RuntimeException("Invalid token at readin_line " + readin_line + " starting at position " + startin_pos);
        }
    }

    // check the next token w/o moving ahead
    public Token peekToken() {
        int curr_pos_save = pos;
        int curr_line_save = readin_line;
        Token token = nextToken();
        pos = curr_pos_save;
        readin_line = curr_line_save;
        return token;
    }

    public boolean is_valid_state(Set<State> states) {
        return states.stream().anyMatch(State::valid_state_check);
    }

    // Resolve the tokentype  based on the precedence
    public TokenType getTokenType(Set<State> states) { 
        TokenType[] precedence = new TokenType[] { 
            TokenType.COMMENT,
            TokenType.KEYWORD, 
            TokenType.CONSTANTS, 
            TokenType.DECIMAL, 
            TokenType.EXPONENT, 
            TokenType.OPERATOR, 
            TokenType.PUNCTUATION, 
            TokenType.GLOBAL, 
            TokenType.LOCAL, 
            TokenType.TRUE, 
            TokenType.FALSE, 
            TokenType.BOOL, 
            TokenType.CHAR, 
            TokenType.STRING, 
            TokenType.IDENTIFIER 
        };
        for (TokenType tok_type : precedence) {
            if (states.stream().anyMatch(s -> s.valid_state_check() && s.get_TokenType() == tok_type)) {
                return tok_type;
            }
        }
        return TokenType.IDENTIFIER;
    }
    
    //helper function to implement rounding off on the 5dp float
    private String roundOffDecimal(String value) {
        if (value.contains(".")) {
            String[] parts = value.split("\\.");
            if (parts[1].length() > 5) {
                double number = Double.parseDouble(value);
                // Round to 5 decimal places
                number = Math.round(number * 1e5) / 1e5;
                value = String.valueOf(number);
            }
        }
        return value;
    }
}