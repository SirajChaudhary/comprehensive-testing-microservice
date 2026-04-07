# Code Coverage Guide (JaCoCo)

## What is Code Coverage?

Code coverage is a measure of how much of your application code is executed when running automated tests.

In simple terms:
- It shows which parts of your code are tested
- It highlights untested code areas
- It helps improve test quality and confidence

## Why Code Coverage is Important

- Identifies untested code paths  
- Improves test quality  
- Reduces bugs in production  
- Provides confidence during refactoring  

## What Does Code Coverage Measure?

JaCoCo provides multiple coverage metrics:

### 1. Line Coverage
- Percentage of executed lines of code  
- Most commonly used metric  

### 2. Branch Coverage
- Checks if all branches (if/else, switch) are executed  

### 3. Method Coverage
- Verifies if methods are invoked during tests  

### 4. Class Coverage
- Ensures classes are used in test execution  

## Tool Used: JaCoCo

We use JaCoCo (Java Code Coverage):

- Lightweight and widely used  
- Integrates with Maven and Gradle  
- Generates HTML reports  
- Supports CI/CD integration  

## How JaCoCo Works

1. Attaches an agent before tests run  
2. Tracks executed code during test execution  
3. Generates a report after tests complete  

## JaCoCo Maven Plugin Configuration

Add the following plugin to your pom.xml:

```xml
<!-- JaCoCo plugin for code coverage reporting -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>

    <configuration>
        <!-- Exclude non-business logic classes from code coverage -->
        <excludes>
            <!-- DTO classes (data holders, no business logic) -->
            <exclude>**/dto/**</exclude>
            <!-- Entity classes (JPA mappings, no business logic) -->
            <exclude>**/model/**</exclude>
            <!-- Spring Boot application entry point class (bootstrapping only, no business logic) -->
            <exclude>**/*Application*</exclude>
        </excludes>
    </configuration>

    <executions>

        <!-- Attach JaCoCo agent to collect coverage during test execution -->
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>

        <!-- Generate coverage report after tests are executed -->
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>

        <!-- Enforce minimum code coverage threshold -->
        <execution>
            <id>check</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <!-- Fail build if overall line coverage is below 70% -->
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>

    </executions>
</plugin>
```

## How to Run Code Coverage

### Step 1: Run Tests

```bash
mvn clean test
```

### Step 2: Run Coverage Check (Optional but Recommended)

```bash
mvn verify
```

## Where to Find the Coverage Report

After running tests, open:

```
target/site/jacoco/index.html
```

Open this file in your browser to view:

- Coverage percentage  
- Covered vs missed lines  
- Class-wise breakdown  

## Understanding the Report

The report shows:

- Green → Covered code  
- Red → Missed code  
- Yellow → Partially covered  

## Recommended Coverage Targets

| Layer        | Target Coverage |
|-------------|----------------|
| Service     | 80–90%         |
| Controller  | 70–80%         |
| Mapper      | ~100%          |
| Overall     | 70%+           |

## Excluding Unnecessary Code

Some classes should not be part of coverage:

- DTOs (data-only classes)  
- Entities (no business logic)  

Example:

```xml
<exclude>**/dto/**</exclude>
<exclude>**/model/**</exclude>
```

## Enforcing Coverage in Build

The check goal ensures:

- Build fails if coverage < 70%  
- Maintains quality standards  

## Best Practices

- Focus on testing business logic (service layer)  
- Avoid writing tests just to increase coverage  
- Do not test simple DTOs or entities  
- Use integration tests to improve real coverage  

## Summary

- Code coverage helps measure test effectiveness  
- JaCoCo is used to generate reports  
- Reports help identify untested code  
- Coverage thresholds enforce quality  
