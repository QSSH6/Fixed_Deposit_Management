---
description: 'Inspect the fixed-deposit module, add focused automated tests, and verify that the complete suite passes'
mode: 'agent'
agent: 'agent'
tools: ['codebase', 'search', 'editFiles', 'runCommands']
argument-hint: 'focus=<feature, risk, or class to test>'
---

## 1. Role

Act as a senior Java QA engineer reviewing a banking application. Prioritize
financial correctness, deterministic tests, boundary behaviour, and readable
failure messages.

## 2. Task

Inspect the current implementation and existing tests. Add the smallest useful
set of automated tests for `${input:focus:the highest-risk untested behavior}`.
Run the full test suite after editing and report the result.

## 3. Context

- Production code: [src](../../src)
- Existing test suite:
  [FixedDepositTestEngineerSuite.java](../../test/FixedDepositTestEngineerSuite.java)
- Project rules and commands: [README.md](../../README.md)
- Sample console input: [sample-input.txt](../../sample-input.txt)
- The project uses Java 17, the default package, `BigDecimal`, RMB, and a
  zero-dependency test runner.
- Valid annual rates are from `-20%` through `20%`, inclusive.
- The system supports replaceable simple-interest and compound-interest
  policies through `InterestCalculator`.

## 4. Constraints and output

1. Preserve production behaviour and do not add Maven, Gradle, JUnit, or other
   dependencies.
2. Extend the existing test suite instead of creating a second test framework.
3. Cover relevant normal, boundary, invalid-input, and exception cases without
   duplicating tests that already exist.
4. Compare `BigDecimal` values with `compareTo`, not `double` tolerances.
5. Keep tests deterministic and use clear Arrange-Act-Assert logic.
6. Compile and run with:

   ```bash
   javac -Xlint:all -d out src/*.java test/*.java
   java -cp out FixedDepositTestEngineerSuite
   ```

7. Finish with a concise summary containing:
   - files inspected;
   - tests added and why;
   - compile/test result;
   - any remaining untested risk.
