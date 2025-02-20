import Lexer.*;
/*
 * Thiis is the main that runs the whole compiler created for our language.
 */

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class App {
    public static void main(String[] args) {
        //dynamically define keywords
        Map<String, TokenType> keywords = new HashMap<>();
        keywords.put("cap", TokenType.FALSE);
        keywords.put("fax", TokenType.TRUE);
        keywords.put("brokestr", TokenType.CHAR);
        keywords.put("fetch", TokenType.KEYWORD); // input keyword
        keywords.put("yap", TokenType.KEYWORD);   // output keyword
        keywords.put("boolulu", TokenType.BOOL);
        keywords.put("yeet", TokenType.KEYWORD);  // return keyword
        keywords.put("digi", TokenType.KEYWORD);  // int keyword
        keywords.put("pointdigi", TokenType.KEYWORD); // float keyword

        // file handliing
        String input = "";
        try {
            File file = new File(".\\input.txt"); 
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                input += scanner.nextLine() + "\n";
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            return;
        }

        // buildingdfa and then callling the lexer for tokenizing
        Lexer lexer = new Lexer(input, keywords); 
        lexer.dfa_machine.display_transTable(input);

        
        //  outside main -> global, after in main -> local
        SymbolTable symbolTable = new SymbolTable();
        Token prev_token = null;
        Stack<String> scope_stack = new Stack<>();      //keep track of locality
        scope_stack.push("global");  //global-> bottom
        boolean inside_main = false;       //to check if in main or not

        int count_tok = 0;
        try {
            Token token;
            do {
                token = lexer.nextToken();
                System.out.println(token);
                count_tok++;
                
                // for function-> if identifier followed by ()
                if (token.tok_type == TokenType.IDENTIFIER) {
                    Token check_nect_tok = lexer.peekToken();
                    if (check_nect_tok.tok_type == TokenType.PUNCTUATION && check_nect_tok.code_val.equals("(")) {
                        
                        //if main function-> scope to local
                        if (token.code_val.equals("main")) {
                            symbolTable.addIdentifier(token.code_val, scope_stack.peek());
                            inside_main = true;
                        } 
                        else {
                            symbolTable.addIdentifier(token.code_val, scope_stack.peek());
                        }
                        lexer.nextToken();
                        prev_token = token;
                        continue;
                    }
                }

                //scope on punctuation basis
                if (token.tok_type == TokenType.PUNCTUATION) {
                    if (token.code_val.equals("{")) {
                        if (inside_main) {
                            scope_stack.push("local");
                        }
                    } 
                    else if (token.code_val.equals("}")) {
                        // for local scope-> pop as its on top and global->bottom
                        if (scope_stack.size() > 1) {
                            scope_stack.pop();
                            if (scope_stack.size() == 1) {
                                inside_main = false;
                            }
                        }
                    }
                }

                // Use current scope for variable declarations.
                String curr_scope = scope_stack.peek();

                // symbol table population logic 
                switch (token.tok_type) {
                    case IDENTIFIER:
                        if (prev_token != null && prev_token.tok_type == TokenType.KEYWORD) {
                            String dataType = prev_token.code_val.equals("digi") ? "INT" : "FLOAT";
                            Token nextToken = lexer.nextToken();
                            if (nextToken.tok_type == TokenType.OPERATOR && nextToken.code_val.equals("=")) {
                                List<Token> exprTokens = new ArrayList<>();
                                Token exprToken;
                                do {
                                    exprToken = lexer.nextToken();
                                    if (exprToken.tok_type == TokenType.PUNCTUATION && exprToken.code_val.equals(";")) {
                                        break;
                                    }
                                    exprTokens.add(exprToken);
                                } while (exprToken.tok_type != TokenType.END_OF_FILE);

                                String computedValue = calculate_arithematic(exprTokens);
                                symbolTable.enter_variable(token.code_val, dataType, curr_scope, computedValue);
                            }
                        }
                        break;
                    case CONSTANTS:
                        symbolTable.addNumber(token.code_val, "INT", curr_scope);
                        break;
                    case OPERATOR:
                        symbolTable.addOperator(token.code_val);
                        break;
                    case KEYWORD:
                    case BOOL:
                    case CHAR:
                        symbolTable.addKeyword(token.code_val);
                        break;
                    case COMMENT:
                        symbolTable.addComment(token.code_val, token.code_val.contains("/*") ? "Multiline" : "Single-line");
                        break;
                    case PUNCTUATION:
                        // Punctuation already used for scope management; still, add if desired.
                        symbolTable.addPunctuation(token.code_val);
                        break;
                    default:
                        break;
                }
                
                prev_token = token;
            } while (token.tok_type != TokenType.END_OF_FILE);

            System.out.println("\nTotal tokens: " + count_tok);
            symbolTable.disp_symbtable();
        } 
        catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Helper method to do arithmetic operations/calculations
    private static String calculate_arithematic(List<Token> tokens) {
        Stack<Double> values = new Stack<>();
        Stack<Token> ops = new Stack<>();

        for (Token token : tokens) {
            if (token.tok_type == TokenType.CONSTANTS || token.tok_type == TokenType.DECIMAL || token.tok_type == TokenType.EXPONENT) {
                values.push(Double.parseDouble(token.code_val));
            } else if (token.tok_type == TokenType.OPERATOR) {
                while (!ops.isEmpty() && hasPrecedence(token.code_val, ops.peek().code_val)) {
                    applyOp(values, ops.pop().code_val);
                }
                ops.push(token);
            }
        }

        while (!ops.isEmpty()) {
            applyOp(values, ops.pop().code_val);
        }

        return values.isEmpty() ? "0" : String.valueOf(values.pop());
    }

    // Helper method to check operator precedence
    private static boolean hasPrecedence(String op1, String op2) {
        if (op2.equals("(") || op2.equals(")")) return false;
        return (!((op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"))));
    }

    // Helper method to apply an operation
    private static void applyOp(Stack<Double> values, String op) {
        double b = values.pop();
        double a = values.isEmpty() ? 0 : values.pop();
        switch (op) {
            case "+":
                values.push(a + b);
                break;
            case "-":
                values.push(a - b);
                break;
            case "*":
                values.push(a * b);
                break;
            case "/":
                values.push(a / b);
                break;
            default:
                throw new RuntimeException("Unknown operator: " + op);
        }
    }
}