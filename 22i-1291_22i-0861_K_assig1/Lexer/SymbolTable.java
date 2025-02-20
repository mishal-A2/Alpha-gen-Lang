package Lexer;
/* This file reads upon the tokenization done by the lexer and genrates a symbol table that identifies the scope of the functions and variables. 
   It identifies the fucntions, types of operators, the value of the var is arithematic operations performed. it also shows the memory.
   Utilizing the file: Lexer.java
 */
import java.util.*;

public class SymbolTable {
    private List<SymbolTableEntry> table_entries = new ArrayList<>();
    private int memoryCounter = 0x1000; //for memory starting from 0x100

    public static class SymbolTableEntry {
        String name;
        String dataType;
        String type;
        String scope;
        String memory;
        String extra_info;

        @Override
        public String toString() {
            return String.format("%-30s | %-20s | %-8s | %-10s | %s",
            name, type + " (" + dataType + ")", scope, memory, extra_info);
        }
    }

//adding identifier-> variable
    public void enter_variable(String name, String dataType, String scope, String var_value) {
        //check if variable is valid
        if (!name.matches("[a-z]+")) {
            throw new RuntimeException("Invalid variable name: '" + name + "'. Must be lowercase letters only.");
        }
    
        SymbolTableEntry table_entry = new SymbolTableEntry();
        table_entry.name = name;
        table_entry.dataType = dataType;
        table_entry.type = "variable";
        table_entry.scope = scope;
        table_entry.memory = String.format("0x%04X", memoryCounter);
        table_entry.extra_info = "Assigned value: " + var_value;
        table_entries.add(table_entry);
        memoryCounter += 4;
    }

    //adding constant-> int, float
    public void addNumber(String value, String dataType, String scope) {
        SymbolTableEntry table_entry = new SymbolTableEntry();
        table_entry.name = value;
        table_entry.dataType = dataType;
        table_entry.type = "constant";
        table_entry.scope = scope;
        table_entry.memory = "-";
        table_entry.extra_info = "Value: " + value;
        table_entries.add(table_entry);
    }

    //adding operator
    public void addOperator(String operator) {
        SymbolTableEntry table_entry = new SymbolTableEntry();
        table_entry.name = operator;
        table_entry.dataType = "OPERATOR";
        table_entry.type = operator.equals("=") ? "assignment operator" : "arithmetic operator";
        table_entry.scope = "-";
        table_entry.memory = "-";
        table_entry.extra_info = "Operator: " + operator;
        table_entries.add(table_entry);
    }

    //adding keywords
    public void addKeyword(String keyword) {
        SymbolTableEntry table_entry = new SymbolTableEntry();
        table_entry.name = keyword;
        table_entry.dataType = "KEYWORD";
        table_entry.type = "keyword";
        table_entry.scope = "-";
        table_entry.memory = "-";
        if (keyword.equals("fetch")) {
            table_entry.extra_info = "Input keyword";
        } else if (keyword.equals("yap")) {
            table_entry.extra_info = "Output keyword";
        } else {
            table_entry.extra_info = "Keyword: " + keyword;
        }
        table_entries.add(table_entry);
    }

    //adding comments-> multi line or line
    public void addComment(String value, String commentType) {
        SymbolTableEntry table_entry = new SymbolTableEntry();
        table_entry.name = value;
        table_entry.dataType = "COMMENT";
        table_entry.type = "comment";
        table_entry.scope = "-";
        table_entry.memory = "-";
        table_entry.extra_info = commentType + " comment";
        table_entries.add(table_entry);
    }

    //adding identifiers-> general
    public void addIdentifier(String name, String scope) {
        SymbolTableEntry table_entry = new SymbolTableEntry();
        table_entry.name = name;
        table_entry.dataType = "FUNCTION";
        table_entry.type = "function";
        table_entry.scope = scope;
        table_entry.memory = String.format("0x%04X", memoryCounter);
        table_entry.extra_info = "Function";
        table_entries.add(table_entry);
        memoryCounter += 4;
    }

    //adding punctuations/syntax
    public void addPunctuation(String punctuation) {
        SymbolTableEntry table_entry = new SymbolTableEntry();
        table_entry.name = punctuation;
        table_entry.dataType = "PUNCTUATION";
        table_entry.type = "punctuation";
        table_entry.scope = "-";
        table_entry.memory = "-";
        table_entry.extra_info = "Punctuation";
        table_entries.add(table_entry);
    }


    public void disp_symbtable() {
        System.out.println("\nSymbol Table:");
        System.out.printf("%-25s | %-20s | %-12s | %-12s | %-30s\n",
        "Name", "Data Type", "Scope", "Memory", "Additional Info");
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        for (SymbolTableEntry table_entry : table_entries) {
            System.out.println(table_entry);
        }
    }
}
