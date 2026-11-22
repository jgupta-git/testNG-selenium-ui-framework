package com.saucedemo.base;
import com.saucedemo.utils.TestUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Base class: every test class extends this.
 * - It stores the login URL.
 * - Before each test it opens Chrome and goes to the login URL.
 * - After each test it closes Chrome.
 * - It also logs what happens at suite, test, class, group and method level.
 *
 * Order of the hooks for one run (roughly):
 *   @BeforeSuite -> @BeforeTest -> @BeforeClass -> @BeforeGroups
 *     -> for each test: @BeforeMethod -> @Test -> @AfterMethod
 *   -> @AfterGroups -> @AfterClass -> @AfterTest -> @AfterSuite
 */
public class BaseTest {

    // The URL of the page under test.
    protected static final String LOGIN_URL = "https://www.saucedemo.com/";

    // The browser. Each test class that extends this class can use it.
    protected WebDriver driver;

    // ---------- SUITE: once for the whole run ----------

    @BeforeSuite(alwaysRun = true)
    public void suiteStart() {
        TestUtils.logToReport("[SUITE START] Basics suite: login and products page tests on saucedemo.com.");
    }

    @AfterSuite(alwaysRun = true)
    public void suiteEnd() {
        TestUtils.logToReport("[SUITE END] Basics suite finished. Check target/screenshots for saved pictures.");
    }

    // ---------- TEST: once per <test> block, like login test (command-line runs count as one block) ----------

    @BeforeTest(alwaysRun = true)
    public void testStart(ITestContext context) {
        TestUtils.logToReport("[TEST START] Block '" + context.getName() + "' is starting.");
    }

    @AfterTest(alwaysRun = true)
    public void testEnd(ITestContext context) {
        TestUtils.logToReport("[TEST END] Block '" + context.getName() + "' finished. Passed: "
                + context.getPassedTests().size() + ", Failed: " + context.getFailedTests().size()
                + ", Skipped: " + context.getSkippedTests().size());
    }

    // ---------- CLASS: once per test class ----------

    @BeforeClass(alwaysRun = true)
    public void classStart() {
        TestUtils.logToReport("[CLASS START] " + getClass().getSimpleName() + " is starting.");
    }

    @AfterClass(alwaysRun = true)
    public void classEnd() {
        TestUtils.logToReport("[CLASS END] " + getClass().getSimpleName() + " is finished.");
    }

    // ---------- GROUP: once before and after the first and last test of each group ----------

    @BeforeGroups(groups = "smoke", alwaysRun = true)
    public void smokeGroupStart() {
        TestUtils.logToReport("[GROUP START: smoke] Quick check. Login works and the products page loads.");
    }

    @AfterGroups(groups = "smoke", alwaysRun = true)
    public void smokeGroupEnd() {
        TestUtils.logToReport("[GROUP END: smoke] Quick check finished.");
    }

    @BeforeGroups(groups = "negative", alwaysRun = true)
    public void negativeGroupStart() {
        TestUtils.logToReport("[GROUP START: negative] Bad-path checks. Blocked users must see an error.");
    }

    @AfterGroups(groups = "negative", alwaysRun = true)
    public void negativeGroupEnd() {
        TestUtils.logToReport("[GROUP END: negative] Bad-path checks finished.");
    }

    @BeforeGroups(groups = "regression", alwaysRun = true)
    public void regressionGroupStart() {
        TestUtils.logToReport("[GROUP START: regression] Full check. Every basics test runs.");
    }

    @AfterGroups(groups = "regression", alwaysRun = true)
    public void regressionGroupEnd() {
        TestUtils.logToReport("[GROUP END: regression] Full check finished.");
    }

    // ---------- METHOD: around every @Test ----------

    // @BeforeMethod runs before EVERY @Test method.
    // alwaysRun = true: run this even when we filter by group (smoke, negative...).
    
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        TestUtils.logToReport("[TEST START] " + method.getName() + " - " + describe(method));
        driver = new ChromeDriver();          // open Chrome
        driver.manage().window().maximize();  // make the window big
        driver.get(LOGIN_URL);                // go to the login page
    }

    // @AfterMethod runs after EVERY @Test method, even if the test failed.
    
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        TestUtils.logToReport("[TEST END] " + result.getName() + " -> " + statusName(result.getStatus()));
        if (driver != null) {
            try {
                driver.quit();                // close Chrome
            } catch (RuntimeException e) {
                // Closing can time out when many browsers run in parallel.
                // Log it, but do not fail the test because of cleanup.
                TestUtils.logToReport("Warning: browser did not close cleanly: " + e.getMessage());
            }
        }
    }

    // Reads the description="..." text from the @Test annotation.
    
    private static String describe(Method method) {
        Test test = method.getAnnotation(Test.class);
        return (test == null || test.description().isEmpty()) ? "(no description)" : test.description();
    }

    private static String statusName(int status) {
        switch (status) {
            case ITestResult.SUCCESS: return "PASSED";
            case ITestResult.FAILURE: return "FAILED";
            case ITestResult.SKIP:    return "SKIPPED";
            default:                  return "UNKNOWN";
        }
    }
}
