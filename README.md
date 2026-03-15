# Java NFA Builder & Regex Converter

A Java project containing two programs that construct and test
Nondeterministic Finite Automata (NFAs). Both programs print a
step-by-step trace of the NFA and allow you to test strings against it.

## How to compile & run

**Compile:**
```bash
javac problem1/*.java
javac problem2/*.java
```

**Run Problem 1:**
```bash
java problem1.Main
```

**Run Problem 2:**
```bash
java problem2.Main
```

When prompted, enter the name of your input file.

## Problem 1 — NFA from language strings

Reads a set of strings from a file and builds an NFA that accepts
exactly those strings. You can then test any string to see if it
is accepted or rejected.

**Input file format:**
```
a,b
ab
ba
a
```
- First line: the alphabet, comma-separated
- Remaining lines: strings that the NFA should accept

**Example interaction:**
```
Enter the input file name: input.txt
Alphabet: [a, b]

Language L contains 3 strings:
  'ab'
  'ba'
  'a'

Enter a string to test (or quit to exit): ab
Result: accept

Enter a string to test (or quit to exit): quit
End program
```

## Problem 2 — NFA from regular expression

Reads a regular expression from a file and converts it to an NFA
using Thompson's construction. Supports the following operators:

| Operator | Meaning | Example |
|----------|---------|---------|
| `+` | Union | `a+b` matches `a` or `b` |
| `*` | Kleene star | `a*` matches zero or more `a` |
| `()` | Grouping | `(ab)*` matches zero or more `ab` |
| concatenation | Sequence | `ab` matches `a` then `b` |

**Input file format:**
```
(a+b)*ab
```
- One line containing the regular expression

**Example interaction:**
```
Enter the input file name: regex.txt
Input Regular Expression: (a+b)*ab

Enter string to test (or 'quit' to exit): aab
Result: accept

Enter string to test (or 'quit' to exit): quit
Program ended.
```

## How it works

**Problem 1** (`problem1/`)
- `NFABuilder.java` — creates a separate path in the NFA for each
  string using lambda transitions off a shared start state
- `NFA.java` — tracks states, transitions, and accept states; uses
  lambda closure to simulate nondeterminism
- `Main.java` — reads the file, builds the NFA, and runs the test loop

**Problem 2** (`problem2/`)
- `RegexToNFA.java` — recursively parses the regex and applies
  Thompson's construction for union, concatenation, and Kleene star
- `NFA.java` — same NFA simulation as Problem 1
- `Main.java` — reads the regex, runs the converter, and tests strings

## Files
```
problem1/
  ├── NFA.java
  ├── NFABuilder.java
  └── Main.java

problem2/
  ├── NFA.java
  ├── RegexToNFA.java
  └── Main.java
```

## Technologies
- Java
- Nondeterministic finite automata
- Thompson's construction
- Lambda (epsilon) closure
- Recursive parsing
