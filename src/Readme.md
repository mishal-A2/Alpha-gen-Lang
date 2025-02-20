# CS4031 Compiler Construction - Lexical Analyzer

## Overview  
A lexical analyzer implementation for a custom programming language, developed as part of a compiler front-end. The analyzer tokenizes input using NFAs/DFAs, validates syntax, and generates a symbol table. Built in Java with modular components for extensibility.

---

## Key Features  
- **DFA/NFA Architecture**: Combines multiple NFAs (numbers, operators, keywords, etc.) into a unified DFA for efficient tokenization.  
- **Dynamic Keyword Support**: Keywords (`fax`, `cap`, `fetch`) mapped to token types at runtime.  
- **Error Handling**: Line-number tracking and descriptive errors for invalid identifiers/characters.  
- **Symbol Table**: Tracks variables, constants, functions, and operators with simulated memory addresses.  
- **Precision Handling**: Rounds floating-point numbers to 5 decimal places.  

---

## Project Structure  
| File               | Description                                                                 |
|--------------------|-----------------------------------------------------------------------------|
| `App.java`         | Entry point. Reads input, initializes lexer, and prints results.           |
| `Lexer.java`       | Tokenizes input using DFA transitions and validates identifiers.           |
| `DFA.java`         | Implements DFA logic with transition table visualization using NFA.                  |
| `NFA.java`         | Constructs NFAs for regex patterns (integers, floats, comments, etc.).     |
| `SymbolTable.java` | Stores identifiers, constants, and keywords with memory simulation.        |
| `Token.java`       | Defines token structure (type and value).                                  |
| `TokenType.java`   | Enumeration of supported token types (e.g., `INT`, `OPERATOR`, `COMMENT`). |

---

## Language Specifications  
### Data Types  
- **Primitives**: `digi` (int), `pointdigi` (float), `boolulu` (boolean), `brokestr` (char).  
- **Literals**:  
  - Boolean: `fax` (true), `cap` (false).  
  - String: Enclosed in `" "`.  

### I/O Operations  
- **Input**: `fetch(<variable>)`  
- **Output**: `yap(<expression>)`  

### Identifiers  
- Lowercase letters only (e.g., `validVar`, **not** `InvalidVar`).  

### Comments  
- Single-line: `//`  
- Multi-line: `/* ... */`  

### Control Flow  
- **Return**: `yeet <value>`  

---

## Installation & Usage  
### Prerequisites  
- Java JDK 8+  

### Steps  
1. **Compile**:  
   ```bash
   javac App.java Lexer/*.java
   ```  
2. **Run**:  
   ```bash
   java App
   ```  
3. **Input**: Place source code in `input.txt` at the project root.  

---

## Sample Input  
```python
digi main() {
    digi result = 10 * 5;
    yap("Result:", result);
    yeet fax;
}
```

### Expected Output  
1. **Transition Table**: DFA state transitions for the input.  
2. **Tokens**: List of recognized tokens (type and value).  
3. **Symbol Table**: Variables, functions, and constants with scope and memory addresses.  

---

## Development  
- **Authors**: Group 2 (CS4031-Compiler Construction).  
- **Extensibility**: Modular NFA/DFA design allows adding new keywords or token types via configuration.  

---

## License  
Academic use only. Not licensed for commercial distribution.  

--- 

*Refer to inline code comments and Javadoc for implementation details.*