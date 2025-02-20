# 🚀 CS4031 Compiler Construction 

A **lexical analyzer** implementation for a custom programming language--> Gen-alpa lang, developed as part of a compiler front-end. This analyzer tokenizes input using **NFAs/DFAs**, validates syntax, and generates a **symbol table**. Built in **Java**, with modular components for extensibility.

---

## 📌 Features
- ✅ **DFA/NFA Architecture** – Combines multiple NFAs (numbers, operators, keywords, etc.) into a **unified DFA** for efficient tokenization.
- 🔧 **Dynamic Keyword Support** – Keywords (`fax`, `cap`, `fetch`) are mapped to token types at runtime.
- ⚠️ **Error Handling** – Tracks line numbers and provides **descriptive error messages** for invalid identifiers/characters.
- 📖 **Symbol Table** – Stores **variables, constants, functions, and operators** with simulated memory addresses.
- 🎯 **Precision Handling** – **Rounds floating-point numbers** to 5 decimal places.

---

## 📁 Project Structure

| File                | Description                                                              |
|---------------------|--------------------------------------------------------------------------|
| `App.java`         | Entry point – Reads input, initializes lexer, and prints results.        |
| `Lexer.java`       | Tokenizes input using DFA transitions and validates identifiers.         |
| `DFA.java`         | Implements DFA logic and visualizes state transitions using NFA.        |
| `NFA.java`         | Constructs NFAs for regex patterns (integers, floats, comments, etc.).  |
| `SymbolTable.java` | Stores identifiers, constants, and keywords with memory simulation.     |
| `Token.java`       | Defines token structure (type and value).                               |
| `TokenType.java`   | Enumeration of supported token types (`INT`, `OPERATOR`, `COMMENT`, etc.). |

---

## 📜 Language Specifications

### 🔹 Data Types
- **Primitives**: `digi` (int), `pointdigi` (float), `boolulu` (boolean), `brokestr` (char).
- **Literals**:  
  - Boolean: `fax` (true), `cap` (false).  
  - String: Enclosed in `" "`.

### 🔹 I/O Operations
- **Input**: `fetch(<variable>)`
- **Output**: `yap(<expression>)`

### 🔹 Identifiers
- Must contain **lowercase letters only** (`validVar`, **not** `InvalidVar`).

### 🔹 Comments
- **Single-line**: `// this is a comment`
- **Multi-line**:
  ```c
  /* This is a
     multi-line comment */
  ```

### 🔹 Control Flow
- **Return Statement**: `yeet <value>`

---

## 🛠 Installation & Usage

### 🔧 Prerequisites
- **Java JDK 8+**

### 📌 Steps
1️⃣ **Compile:**  
   ```bash
   javac App.java Lexer/*.java
   ```

2️⃣ **Run:**  
   ```bash
   java App
   ```

3️⃣ **Input:**  
   - Place source code in `input.txt` at the project root.

---

## 📝 Sample Code & Expected Output

### ✅ Sample Input
```c
// Sample program
digi main() {
    digi result = 10 * 5;
    yap("Result:", result);
    yeet fax;
}
```

### 🔍 Expected Output
1. **DFA State Transitions** – Visual representation of state changes.
2. **Tokens** – List of recognized tokens (type & value).
3. **Symbol Table** – Variables, functions, and constants with scope & memory addresses.

---

## 🚀 Development & Contribution
- **Authors**: @mishal-A2 N  @ayaanm930 
- **Extensibility**: Easily add new keywords or token types by modifying configurations.

### 🏗 Future Enhancements
- 🚀 Improve performance using **optimized DFA minimization**.
- 🔍 Implement **Parser** for better syntax validation.
- 🔥 Add **support for additional data types and operators**.
- 

---

## 📜 License

**Academic Use Only** – Not licensed for commercial distribution.

📌 *Refer to inline code comments and Javadoc for detailed implementation insights.*

