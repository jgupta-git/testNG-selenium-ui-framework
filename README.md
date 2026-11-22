# UI Test Framework with Selenium + TestNG

A UI test framework that tests the login and products pages of [saucedemo.com](https://www.saucedemo.com). It uses TestNG to show the features that matter in a real suite: lifecycle hooks, groups, priorities, data providers, dependencies, retries, timeouts, parallel runs, listeners and screenshots in the report.

## Overview

Each test class extends a `BaseTest` that opens Chrome before every test, goes to the login page, and closes the browser afterwards. Page objects hold the locators, so tests read like plain steps. Test data lives in one class, and the test report includes a log line and a screenshot for each test.

This project shows how to:

- Use the TestNG lifecycle: `@BeforeSuite`, `@BeforeTest`, `@BeforeClass`, `@BeforeGroups`, `@BeforeMethod` and their `@After` counterparts
- Organise tests with groups (`smoke`, `negative`, `regression`, `advanced`)
- Control order with `priority` and `dependsOnMethods`
- Run one test many times with a data provider or `invocationCount`
- Retry a failing test with a `RetryAnalyzer`
- Check that an exception is thrown with `expectedExceptions`
- Fail a test that runs too slowly with `timeOut`
- Run test classes in parallel from `testng.xml`
- Attach a listener to every test event
- Embed screenshots in the TestNG report

## Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 17+ | Language |
| Selenium | 4.27.0 | Browser automation (Selenium Manager finds ChromeDriver automatically) |
| TestNG | 7.10.2 | Test runner, assertions, reports |
| Maven | 3.x | Build and dependencies |
| Surefire | 3.5.0 | Runs TestNG from Maven |

## Test Scenarios

Test classes are in `src/test/java/com/saucedemo/tests`. "Priority" is the order inside a class (lower runs first).

### Login (`LoginTest`)

| Test | Groups | What it checks |
|---|---|---|
| `validUserCanLogin` | smoke, regression | A valid user lands on the products page |
| `lockedOutUserSeesErrorMessage` | negative, regression | A locked-out user sees the locked-out message |

### Products (`InventoryTest`)

| Test | Priority | Groups | What it checks |
|---|---|---|---|
| `pageHasCorrectTitleAndUrl` | 1 | smoke, regression | Title is "Products" and the URL is the inventory page |
| `pageShowsSixProducts` | 2 | smoke, regression | Six products are listed |
| `productListContainsBackpack` | 3 | regression | "Sauce Labs Backpack" is in the list |
| `addProductsToCartAndCheckCart` | 4 | regression | A data provider list of three products is added, and the cart holds all three. Depends on `pageShowsSixProducts` |

### Advanced (`AdvancedTests`)

| Test | Attribute | What it checks |
|---|---|---|
| `loginRetriesWithCorrectPassword` | `retryAnalyzer`, `dependsOnGroups = smoke` | The first attempt uses a wrong password and fails. The retry uses the right one and passes |
| `loginPageTitleRepeats` | `invocationCount = 3` | The login page title is "Swag Labs" in all 3 runs |
| `addingFakeProductThrowsError` | `expectedExceptions` | Adding a product that isn't on the page throws `IllegalArgumentException`, so the test passes |
| `disabledExample` | `enabled = false` | Never runs |
| `pageTakesTooLongToLoad` | `timeOut = 2000` | Sleeps 3 seconds, so it fails on purpose. Group `timeout-demo` |

A full run executes 11 tests. `disabledExample` is off, and `pageTakesTooLongToLoad` is excluded in `testng.xml`.

## Project Structure

```
testNG-selenium-ui-framework/
├── pom.xml
├── testng.xml                          # Suite file: parallel by class, skips timeout-demo
├── docs/
│   └── test-report.html                # Sample report from a full run
└── src/test/java/com/saucedemo/
    ├── base/
    │   └── BaseTest.java               # Browser setup, teardown, lifecycle logs
    ├── config/
    │   └── TestData.java               # Users, URL, expected messages
    ├── pages/
    │   ├── LoginPage.java
    │   ├── InventoryPage.java          # @FindBy for one element and for lists
    │   └── CartPage.java
    ├── utils/
    │   ├── TestUtils.java              # Waits, logging, screenshots, JS click
    │   ├── RetryAnalyzer.java          # Retries a failing test up to 2 times
    │   └── SimpleListener.java         # Prints an event line for every test
    └── tests/
        ├── LoginTest.java
        ├── InventoryTest.java
        └── AdvancedTests.java
```

## Key Concepts

### Hook order

For one run, TestNG calls the hooks in this order:

```
@BeforeSuite -> @BeforeTest -> @BeforeClass -> @BeforeGroups
  for each @Test: @BeforeMethod -> @Test -> @AfterMethod
@AfterGroups -> @AfterClass -> @AfterTest -> @AfterSuite
```

`@BeforeMethod` opens a fresh browser for every test, so no test depends on another test's browser state.

### Groups and dependencies

Groups are set on each test with `groups = {...}`. Run one group with `-Dgroups=smoke`. `dependsOnMethods` runs a test only after the named test passes. `dependsOnGroups` does the same for a whole group.

### Parallel runs

`testng.xml` uses `parallel="classes"` with 3 threads. Each class runs in its own thread, and the tests inside one class run one after another. That keeps the shared `driver` field safe.

### Reports

- `Reporter.log` output and screenshots from test methods appear in `emailable-report.html`.
- Output from setup and teardown methods appears in the console and in `testng-results.xml`, but not in the HTML report.
- Each screenshot is embedded in the report as base64, so the HTML file works on its own.

## Quick Start

### Prerequisites

- Java 17 or later
- Maven 3.x
- Google Chrome (the driver is downloaded automatically)

### Run

```powershell
mvn test
```

Run one group:

```powershell
mvn test -Dgroups=smoke
```

Run one test:

```powershell
mvn test -Dtest=com.saucedemo.tests.LoginTest#validUserCanLogin
```

Run the timeout demo: remove the `<exclude name="timeout-demo"/>` line from `testng.xml`, then run `mvn test`. The build fails on purpose.

### Output

- Report: `target/surefire-reports/emailable-report.html`
- Screenshots: `target/screenshots/`

## Test Report

A sample report from a full run is in [`docs/test-report.html`](docs/test-report.html). To view it in a browser, open it with [htmlpreview](https://htmlpreview.github.io/?https://github.com/jgupta-git/testNG-selenium-ui-framework/blob/main/docs/test-report.html), or download the file and open it locally.

## Run in Eclipse

Import the project as a Maven project (File → Import → Existing Maven Projects). Right-click `testng.xml` and choose Run As → TestNG Suite.

If Maven reports `invalid target release: 17`, `JAVA_HOME` points to an older JDK. Set it for the session:

```bash
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.12.101-hotspot"
```

## References

- [TestNG documentation](https://testng.org/)
- [Selenium documentation](https://www.selenium.dev/documentation/)
- [Sauce Demo test site](https://www.saucedemo.com/)
