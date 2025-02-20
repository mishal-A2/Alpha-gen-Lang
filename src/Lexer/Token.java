package Lexer;
/* This file assigns the token types to different lexemes and prints the tokens out.
    Utilizing the files: TokenType.java
 */
public class Token {
    public final TokenType tok_type;
    public final String code_val;   //the value of the token in the input code

    public Token(TokenType tok_type, String code_val) {
        this.tok_type = tok_type;
        this.code_val = code_val;
    }

    @Override
    public String toString() {
        return "Token type= " + tok_type + ", Lexeme= '" + code_val ;
    }
}
