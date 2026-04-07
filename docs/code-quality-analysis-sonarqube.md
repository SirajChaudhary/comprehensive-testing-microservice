# SonarQube + JaCoCo Code Coverage Guide

## What is SonarQube?

SonarQube is a platform for static code analysis that helps improve code quality by detecting:

- Bugs
- Code smells
- Vulnerabilities
- Security issues
- Code duplication
- Test coverage (via JaCoCo)

It provides dashboards, reports, and quality gates to ensure code meets defined standards.

## What is Static Code Analysis?

Static code analysis means analyzing code without executing it.

SonarQube scans your code and identifies:
- Logical issues
- Bad practices
- Maintainability problems
- Security risks

## What is JaCoCo?

JaCoCo is a tool that measures code coverage.

It answers:
- How much code is executed by tests?
- Which lines are covered or missed?

## Why Do We Need SonarQube If We Already Have JaCoCo?

| Tool | Purpose |
|------|--------|
| JaCoCo | Measures test coverage |
| SonarQube | Analyzes overall code quality + uses JaCoCo coverage |

JaCoCo only measures coverage, whereas SonarQube provides a complete quality analysis.

## What More SonarQube Covers Beyond JaCoCo?

- Code smells
- Security vulnerabilities
- Maintainability issues
- Duplicate code detection
- Complexity analysis
- Quality gates

## What are Quality Gates?

Quality Gates are rules that define whether your code is acceptable.

Example:
- Coverage ≥ 70%
- No critical bugs
- No security issues

## Coverage Percentage (Industry Standards)

| Layer | Recommended Coverage |
|------|---------------------|
| Service | 80–90% |
| Controller | 70–80% |
| Mapper | ~100% |
| Overall | 70%+ |

## Role of JaCoCo and SonarQube Together

- JaCoCo generates coverage reports
- SonarQube reads those reports
- JaCoCo produces data
- SonarQube analyzes and visualizes

# Step-by-Step Setup

### Step 1: Start SonarQube Server
```
docker run -d -p 9000:9000 sonarqube:community
```

Open:
```
http://localhost:9000
```

Username/password: `admin`

### Step 2: Create Project

Create a local project in SonarQube and note the project key.

### Step 3: Generate (User) Token
```
http://localhost:9000/account/security
```
Example generated token: squ_xxxxx
### Step 4: Add Sonar Plugin (pom.xml)
```
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.10.0.2594</version>
</plugin>
```

### Step 5: Add JaCoCo Plugin (pom.xml)
```
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Step 6: Link JaCoCo with SonarQube (pom.xml)
```
<properties>
    <sonar.coverage.jacoco.xmlReportPaths>
        ${project.build.directory}/site/jacoco/jacoco.xml
    </sonar.coverage.jacoco.xmlReportPaths>
</properties>
```

### Step 7: Run Analysis
```
mvn clean verify sonar:sonar   
-Dsonar.projectKey=comprehensive-testing-microservice   
-Dsonar.host.url=http://localhost:9000   
-Dsonar.token=squ_xxxxx
```
### Step 8: View Results
```
http://localhost:9000
```

## Summary

- JaCoCo → coverage
- SonarQube → full quality analysis
- Together → complete testing + quality ecosystem
