---
description: 'Inspect the fixed-deposit module, add focused automated tests, and verify that the complete suite passes'
mode: 'agent'
agent: 'agent'
tools: ['codebase', 'search', 'editFiles', 'runCommands']
argument-hint: 'focus=<feature, risk, or class to test>'
---

## 1. Task

Inspect the production classes in [src](../../src), the existing
[test suite](../../test/FixedDepositTestEngineerSuite.java), the project
[rules](../../README.md), and the [sample input](../../sample-input.txt).

Identify missing automated-test coverage for
`${input:focus:the highest-risk untested behavior}`. Add the smallest useful
set of tests to the existing suite, execute the complete suite, and report the
verified result. Do not only suggest tests: implement and run them.

## 2. Constraints

- Use Java 17 and the repository's default package.
- Do not add Maven, Gradle, JUnit, external libraries, network calls, or APIs.
- Modify test code only. Do not change production behaviour in `src`.
- Extend `test/FixedDepositTestEngineerSuite.java`; do not create another test
  framework or duplicate an existing test.
- Preserve the project's `BigDecimal`, RMB, customer-name, and annual-rate
  rules. Valid annual rates are `-20%` through `20%`, inclusive.
- Cover the relevant normal, boundary, invalid-input, and exception paths.
- Compare `BigDecimal` values through the existing
  `assertBigDecimalEquals` helper, which uses `compareTo`.
- Follow the existing coding style: four-space indentation, descriptive
  `test...` method names, and deterministic Arrange-Act-Assert test logic.
- Keep the complete test run below five seconds on the local Java 17 runtime.

## 3. Expected Output

1. Update only `test/FixedDepositTestEngineerSuite.java`.
2. Register each new case with:

   ```java
   runTest("descriptive test name",
           FixedDepositTestEngineerSuite::testMethodName);
   ```

3. Implement each test with this signature and return type:

   ```java
   private static void testMethodName()
   ```

4. Reuse the suite's existing assertion and output-capture helpers.
5. Return a concise Markdown summary with exactly these headings:
   - `Files inspected`
   - `Tests added`
   - `Verification result`
   - `Remaining risk`

## 4. Verification

Run these commands from the repository root:

```bash
javac -Xlint:all -d out src/*.java test/*.java
java -cp out FixedDepositTestEngineerSuite
```

The task is complete only when all acceptance criteria pass:

- compilation exits successfully with no warnings;
- every existing and newly added test reports `[PASS]`;
- the final summary reports `0 failed`;
- `git diff -- src` is empty, confirming no production code changed;
- no dependency or build-configuration file was added.
