<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-f89820?logo=openjdk&logoColor=white" alt="Java 17+"/>
  <img src="https://img.shields.io/badge/Selenium-4.27.0-43B02A?logo=selenium&logoColor=white" alt="Selenium 4.27.0"/>
  <img src="https://img.shields.io/badge/TestNG-7.10.2-e0234e" alt="TestNG 7.10.2"/>
  <img src="https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/tests-11%20passing-2ea44f" alt="11 tests passing"/>
</p>

<h1 align="center">🧪 UI Test Framework with Selenium + TestNG</h1>

<p align="center">
  Automated tests for the login and products pages of <a href="https://www.saucedemo.com">saucedemo.com</a>,
  built to show the TestNG features used in real test suites.
</p>

---

## 📊 Test Report

**View the live report:** [jgupta-git.github.io/testNG-selenium-ui-framework/test-report.html](https://jgupta-git.github.io/testNG-selenium-ui-framework/test-report.html)

The report contains each test's result (passed, failed or skipped), its log lines, and a screenshot taken during the test.

---

## 📖 Overview

Each test class extends a `BaseTest` that opens Chrome before every test, goes to the login page, and closes the browser afterwards. Page objects hold the locators, so tests read like plain steps. Test data lives in one class, and every test adds a log line and a screenshot to the report.

### 🎯 This project shows how to

- 🔁 Use the TestNG lifecycle: `@BeforeSuite`, `@BeforeTest`, `@BeforeClass`, `@BeforeGroups`, `@BeforeMethod` and their `@After` counterparts
- 🏷️ Organise tests with groups (`smoke`, `negative`, `regression`, `advanced`)
- 🔢 Control order with `priority` and `dependsOnMethods`
- 📊 Feed data into a test with a `@DataProvider`, or run one test many times with `invocationCount`
- ♻️ Retry a failing test with a `RetryAnalyzer`
- 🎯 Check that an exception is thrown with `expectedExceptions`
- ⏱️ Fail a test that runs too slowly with `timeOut`
- ⚡ Run test classes in parallel from `testng.xml`
- 👂 Attach a listener to every test event
- 📸 Embed screenshots in the TestNG report

---

## 🛠️ Tech Stack

| | Tool | Version | Purpose |
|:-:|---|---|---|
| ☕ | Java | 17+ | Language |
| 🌐 | Selenium | 4.27.0 | Browser automation (Selenium Manager finds ChromeDriver for you) |
| 🧪 | TestNG | 7.10.2 | Test runner, assertions and reports |
| 📦 | Maven | 3.x | Build and dependencies |
| 🚀 | Surefire | 3.5.0 | Runs TestNG from Maven |

---

## ✅ Test Scenarios

Test classes are in `src/test/java/com/saucedemo/tests`. **Priority** is the order inside a class (lower runs first).

### 🔐 Login (`LoginTest`)

| Test | Groups | What it checks |
|---|---|---|
| ✅ `validUserCanLogin` | `smoke`, `regression` | A valid user lands on the products page |
| 🔒 `lockedOutUserSeesErrorMessage` | `negative`, `regression` | A locked-out user sees the locked-out message |

### 🛒 Products (`InventoryTest`)

| Priority | Test | Groups | What it checks |
|:-:|---|---|---|
| 1️⃣ | `pageHasCorrectTitleAndUrl` | `smoke`, `regression` | Title is "Products" and the URL is the inventory page |
| 2️⃣ | `pageShowsSixProducts` | `smoke`, `regression` | Six products are listed |
| 3️⃣ | `productListContainsBackpack` | `regression` | "Sauce Labs Backpack" is in the list |
| 4️⃣ | `addProductsToCartAndCheckCart` | `regression` | A data provider list of three products is added, and the cart holds all three. Depends on `pageShowsSixProducts` |

### 🧬 Advanced (`AdvancedTests`)

| Test | TestNG feature | What it checks |
|---|---|---|
| ♻️ `loginRetriesWithCorrectPassword` | `retryAnalyzer`, `dependsOnGroups = smoke` | First attempt uses a wrong password and fails; the retry uses the right one and passes |
| 🔁 `loginPageTitleRepeats` | `invocationCount = 3` | Login page title is "Swag Labs" in all 3 runs |
| 🎯 `addingFakeProductThrowsError` | `expectedExceptions` | Adding a product that isn't on the page throws `IllegalArgumentException`, so the test passes |
| 💤 `disabledExample` | `enabled = false` | Never runs |
| 💥 `pageTakesTooLongToLoad` | `timeOut = 2000` | Sleeps 3 seconds, so it **fails on purpose**. Group `timeout-demo` |

> [!TIP]
> A full run executes **11 tests**. `disabledExample` is turned off, and `pageTakesTooLongToLoad` is excluded in `testng.xml`.

---

## 🗂️ Project Structure

```
testNG-selenium-ui-framework/
├── 📄 pom.xml
├── 📄 testng.xml                       # Suite file: parallel by class, skips timeout-demo
├── 🖼️ docs/
│   └── test-report.html                # Sample report from a full run
└── 📂 src/test/java/com/saucedemo/
    ├── 🧱 base/
    │   └── BaseTest.java               # Browser setup, teardown, lifecycle logs
    ├── ⚙️ config/
    │   └── TestData.java               # Users, URL, expected messages
    ├── 📑 pages/
    │   ├── LoginPage.java
    │   ├── InventoryPage.java          # @FindBy for one element and for lists
    │   └── CartPage.java
    ├── 🧰 utils/
    │   ├── TestUtils.java              # Waits, logging, screenshots, JS click
    │   ├── RetryAnalyzer.java          # Retries a failing test up to 2 times
    │   └── SimpleListener.java         # Prints an event line for every test
    └── 🧪 tests/
        ├── LoginTest.java
        ├── InventoryTest.java
        └── AdvancedTests.java
```

---

## 💡 Key Concepts

### 🔁 Hook order

For one run, TestNG calls the hooks in this order:

```
@BeforeSuite → @BeforeTest → @BeforeClass → @BeforeGroups
  for each @Test: @BeforeMethod → @Test → @AfterMethod
@AfterGroups → @AfterClass → @AfterTest → @AfterSuite
```

`@BeforeMethod` opens a fresh browser for every test, so no test depends on another test's browser state.

### 🏷️ Groups and dependencies

Groups are set on each test with `groups = {...}`. Run one group with `-Dgroups=smoke`. `dependsOnMethods` runs a test only after the named test passes. `dependsOnGroups` does the same for a whole group.

### 🔎 Locators

Page objects hold the locators for page actions, such as typing and clicking. Some explicit waits in the test classes use locators directly, for example `By.id("login-button")`. This is a shortcut for the test classes, so a locator change may need updates in both places.

### ⚡ Parallel runs

`testng.xml` uses `parallel="classes"` with 3 threads. Each class runs in its own thread, and the tests inside one class run one after another. That keeps the shared `driver` field safe.

---

## 🚀 Quick Start

### Prerequisites

- ☕ Java 17 or later
- 📦 Maven 3.x
- 🌐 Google Chrome (the driver is downloaded automatically)

### ▶️ Run

Run the whole suite:

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

> [!WARNING]
> To run the timeout demo, remove the `<exclude name="timeout-demo"/>` line from `testng.xml`, then run `mvn test`. The build **fails on purpose**.

### 📁 Output

- 📄 Report: `target/surefire-reports/emailable-report.html`
- 📸 Screenshots: `target/screenshots/`

---

## 🧭 Run in Eclipse

1. Import the project: **File → Import → Existing Maven Projects**.
2. Right-click `testng.xml` and choose **Run As → TestNG Suite**.

> [!IMPORTANT]
> If Maven reports `invalid target release: 17`, `JAVA_HOME` points to an older JDK. Set it for the session:
>
> ```bash
> export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.12.101-hotspot"
> ```

---

## 📚 References

- 🧪 [TestNG documentation](https://testng.org/)
- 🌐 [Selenium documentation](https://www.selenium.dev/documentation/)
- 🛒 [Sauce Demo test site](https://www.saucedemo.com/)
