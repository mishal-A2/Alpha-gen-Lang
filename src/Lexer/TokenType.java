package Lexer;

public enum TokenType { 
    INT, FLOAT, BOOL, CHAR, STRING,
    
    CONSTANTS, DECIMAL, OPERATOR, IDENTIFIER, PUNCTUATION, EXPONENT, COMMENT, KEYWORD,
    
    GLOBAL, LOCAL, RETURN, END_OF_FILE, TRUE, FALSE
    }
