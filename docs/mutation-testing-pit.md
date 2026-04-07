# PIT Mutation Testing (Test Quality Analysis)

## Overview

This module demonstrates Mutation Testing using PIT (pitest) to evaluate the quality and effectiveness of test cases.

Unlike traditional coverage tools, mutation testing ensures that tests are strong enough to detect real defects.

## What is mutation testing ? 

Mutation Testing is a technique used to measure how effective your test cases are.

It works by:
- Making small changes (mutations) in your code
- Running your tests
- Checking if tests fail

Simple Idea:
- If tests fail → Good tests
- If tests pass → Weak tests

Example:

Original code:
```
if (balance > 0)
```
Mutated code:
```
if (balance >= 0)
```
If tests still pass, your test missed a bug.

## What is PIT (PITEST)?

PIT is a mutation testing tool for Java.

It automatically:
- Creates code mutations
- Runs your test suite
- Generates a report showing:
  - Killed mutations (good)
  - Survived mutations (bad)

## Why do we need PIT?

Traditional tools like JaCoCo only measure which lines of code were executed.

But they do not check:
- Whether assertions are strong
- Whether logic is truly validated

Problem with coverage alone:
High coverage does not mean good tests.

PIT solves this by:
- Detecting weak tests
- Improving assertion quality
- Ensuring logic is properly validated
- Providing confidence in test effectiveness

## PIT vs JaCoCo vs SonarQube

JaCoCo:
- Measures code coverage
- Shows which lines are executed

SonarQube:
- Provides code quality analysis
- Uses JaCoCo coverage data
- Detects bugs and code smells

PIT:
- Measures test quality
- Checks whether tests can detect code changes

Key Difference:
- JaCoCo → How much code is tested
- PIT    → How well code is tested

## How to add PIT in your project ? 

Step 1: Add PIT Plugin

Add the following plugin in pom.xml:
```
<!-- PIT (Mutation Testing): evaluates test quality by introducing code mutations and verifying if tests detect them -->
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.15.0</version>
    <configuration>
        <targetClasses>
            <param>com.example.comprehensivetesting.*</param>
        </targetClasses>
        <targetTests>
            <param>com.example.comprehensivetesting.*</param>
        </targetTests>
    </configuration>
</plugin>
```

Step 2: Run Mutation Tests

Run the following command:
```
mvn org.pitest:pitest-maven:mutationCoverage
```

Step 3: View Report

After execution, open:
```
target/pit-reports/index.html
```
The report shows:
- Mutation coverage percentage
- Killed mutations
- Survived mutations
- Detailed analysis per class

## How to read PIT report ?

Killed Mutations:
- Tests detected the change
- Indicates strong tests

Survived Mutations:
- Tests did not detect the change
- Indicates weak or missing tests

Mutation Score:
Mutation Coverage = (Killed Mutations / Total Mutations) * 100

Ideal Range:
- 70% – 90% is considered good
- Below 60% needs improvement

## Where to improve tests ?

If mutations survive:
- Add better assertions
- Cover edge cases
- Test conditional logic
- Validate business rules more thoroughly

## Summary

- Mutation testing evaluates test effectiveness
- PIT identifies weak tests
- Complements JaCoCo and SonarQube
- Helps build reliable and robust test suites

Final Insight:
- Coverage tells you tests exist
- Mutation testing tells you tests are effective
