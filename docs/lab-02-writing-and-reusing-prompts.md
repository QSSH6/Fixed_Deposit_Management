# Lab 02: Writing and Reusing Prompts

## Function selected

Generate focused automated tests for the Fixed Deposit Management project.
This is a recurring task whenever an interest policy, validation rule, or
reporting requirement changes.

## Vague prompt

```text
Add some tests for this bank project.
```

### Output from the vague prompt

The vague request produced a generic suggestion to create JUnit tests for a
normal positive-interest calculation. It did not identify the repository's
zero-dependency test runner, the valid `-20%` to `20%` rate range, RMB rules,
the `InterestCalculator` implementations, or the required compile command.
Following it literally would introduce a new dependency and duplicate an
existing happy-path test.

## Precise prompt using the four-part structure

The reusable prompt is stored at
[generate-fixed-deposit-tests.prompt.md](../.github/prompts/generate-fixed-deposit-tests.prompt.md).

Its four parts are:

1. **Task** - uses specific action verbs: inspect, identify, add, execute, and
   report.
2. **Constraints** - defines Java version, framework restrictions, five-second
   performance limit, coding style, financial rules, and the production-code
   boundary.
3. **Expected Output** - specifies the only editable file, test registration
   format, method signature, `void` return type, and final Markdown headings.
4. **Verification** - specifies compile/test commands and measurable acceptance
   criteria: no warnings, all tests passing, `0 failed`, no source changes, and
   no new dependencies.

## Invocation

In Copilot Chat, run:

```text
/generate-fixed-deposit-tests focus=compound-interest boundaries, including zero, negative, and fractional terms
```

The file can also be opened and run with the play button, or selected through
**Chat: Run Prompt**.

## Output from the precise prompt

The prompt inspected both calculators and the existing suite before editing.
It found that annual compound interest had one positive, whole-year test but
no coverage for:

- a zero rate;
- a valid negative rate;
- a fractional-year term.

It extended `FixedDepositTestEngineerSuite` with those three assertions,
without changing production code or adding dependencies.

## Comparison

| Criterion | Vague prompt | Precise reusable prompt |
|---|---|---|
| Task | Vague action with no scope | Uses specific action verbs and a supplied test focus |
| Constraints | No language or framework limits | Java 17, zero dependencies, coding style, financial rules, and time limit |
| Expected output | No file or format specified | Exact file, registration form, method signature, return type, and Markdown headings |
| Verification | No command or acceptance criteria | Compile/test commands, no warnings, all pass, no production diff |

## Review of the invoked output

The precise prompt was more useful because it produced compatible, focused,
and verifiable changes. The generated tests passed with the complete suite.
No production behaviour changed.

```text
Audit unit test result: 9 passed, 0 failed, 9 total.
```

Remaining risk: fractional compound interest uses `Math.pow` internally, so
future financial-policy work should define an approved precision method for
fractional compounding periods.

## Front matter

The prompt file includes:

- `description` for discovery;
- `mode: agent` as required by the lab;
- `agent: agent` for current VS Code prompt-file compatibility;
- `tools` for repository inspection, editing, and command execution.
