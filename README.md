# Backend Automation Framework

Multi-module Java **21** API automation library for LOB projects (TestNG or BDD/Cucumber).

```
GroupId:  com.airtel.api-automation
Version:  1.0.0
Artifact: automation-bundle | automation-core | …
```

## Modules

| Module | Contents |
|--------|----------|
| `automation-exceptions` | `GenericException`, `ErrorCode`, `@CatchException` |
| `automation-logging` | `AirtelLoggingMgr`, `AirtelConfigService`, `Constants` |
| `automation-report` | HTML `ReportHelper`, ChainTest, `ReportSelector` (TESTNG/BDD), FileHelper, Influx, SentinelLogger |
| `automation-data` | Excel / DB / Mongo / Aerospike / JSON / Data helpers |
| `automation-validator` | JSON compare & schema validators |
| `automation-core` | `ApiHelper`, `BaseManager`, WireMock, DTOs |
| `automation-bundle` | Aggregator POM — single LOB dependency |

Source lives under each module (`automation-*/src/main/...`). There is **no** root `src/main`. IDE folders (`.settings`, `.classpath`, `.project`, `.vscode`) are gitignored.

## Add to an LOB (e.g. clm-be-automation)

**All modules:**

```xml
<dependency>
  <groupId>com.airtel.api-automation</groupId>
  <artifactId>automation-bundle</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

**Or core only** (pulls data / validator / report / logging transitively):

```xml
<dependency>
  <groupId>com.airtel.api-automation</groupId>
  <artifactId>automation-core</artifactId>
  <version>1.0.0</version>
</dependency>
```

Until published to Nexus, install locally:

```bash
mvn clean install -DskipTests
```

### Migrate from 1.4.x

Replace:

```xml
<artifactId>api-automation-framework</artifactId>
<version>1.4.x</version>
```

with `automation-bundle` or `automation-core` at `1.0.0`. Packages remain under `com.airtel.helper.*`.

## Reporting (TESTNG vs BDD)

LOB chooses once:

```properties
# src/test/resources/automation-report.properties
automation.report.framework=TESTNG
# or
automation.report.framework=BDD
```

Or: `-Dautomation.report.framework=BDD`

| Mode | What runs |
|------|-----------|
| `TESTNG` | HTML `ReportHelper` + ChainTest via `AirtelReportListener` |
| `BDD` | ChainTest Cucumber plugin; HTML suite lifecycle skipped |

**TestNG LOB**

```java
@Listeners(AirtelReportListener.class)
public class BaseTest extends ReportHelper { }
```

**BDD LOB** (add Cucumber plugin dep if needed):

```xml
<dependency>
  <groupId>com.aventstack</groupId>
  <artifactId>chaintest-cucumber-jvm</artifactId>
  <version>1.0.12</version>
</dependency>
```

```java
@CucumberOptions(plugin = {
  ReportFrameworkConfig.CUCUMBER_CHAINTEST_PLUGIN
})
public class RunCukes extends AbstractTestNGCucumberTests { }
```

| Report | Output |
|--------|--------|
| Custom HTML | `./Results/...` |
| ChainTest | `target/chaintest/Index.html` |

### Exception reporting

```java
@Listeners(AirtelReportListener.class) // or CatchExceptionListener alone
@CatchException
public class BaseTest extends ReportHelper { }
```

On failure, framework `GenericException` / `ErrorCode` are logged to the HTML report. The test still fails.

## Suggested LOB layout

```
src/main/java/.../constants
src/main/java/.../controller   # API flows (use ApiHelper / BaseManager)
src/main/java/.../helper
src/main/java/.../dto
src/main/resources/            # configs; override automation-report.properties / chaintest.properties
src/test/java/...              # thin tests or Cucumber glue — no heavy business logic
```

- **TestNG:** test classes extend `ReportHelper` and register `AirtelReportListener`.
- **BDD:** features → step defs → controllers/helpers; reporting via ChainTest plugin + hooks as needed.

## Main capabilities

| Area | Classes |
|------|---------|
| HTTP / RestAssured | `ApiHelper`, `BaseManager` |
| Mocks | `WiremockHelper` |
| Validations | `ApiResponseValidator`, `GenericComparator` |
| Data | `DbHelper`, `MongoHelper`, `AerospikeHelper`, `ExcelHelper`, `TestDataHelper` |
| Config | `AirtelConfigService`, `Constants` |
| Reports | `ReportHelper`, `AirtelReportListener`, ChainTest |

## Sentinel libraries

| Component | Version |
|-----------|---------|
| Subscriber TestNG | 1.1.8 |
| Transformer optimus-java | 1.1.7 |
| Sentinel models | 2.0.7 |
| Sentinel Log4j Appender | 1.0.7 |
