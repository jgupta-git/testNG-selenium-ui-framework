package com.saucedemo.tests;
import com.saucedemo.base.BaseTest;
import com.saucedemo.config.TestData;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.RetryAnalyzer;
import com.saucedemo.utils.TestUtils;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

/**
 * One test for each advanced TestNG attribute.
 *
 * Main run (should pass):  -Dgroups=smoke,advanced
 * Timeout demo (fails on purpose): -Dgroups=timeout-demo
 */
public class AdvancedTests extends BaseTest {

    // Passwords to try, one per attempt. The first attempt is wrong on purpose.
    private static final List<String> PASSWORD_ATTEMPTS =
            List.of("wrong_password", TestData.VALID_PASSWORD);

    private int attemptNumber = 0;

    // 1. retryAnalyzer + dependsOnGroups
    // Runs only if every "smoke" test passed. If the first try fails, RetryAnalyzer runs it again.
    
    @Test(groups = {"advanced"}, dependsOnGroups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Login retries: a wrong password fails the first try, the right one passes the retry")
    public void loginRetriesWithCorrectPassword() {
        String password = PASSWORD_ATTEMPTS.get(Math.min(attemptNumber, PASSWORD_ATTEMPTS.size() - 1));
        attemptNumber++;
        TestUtils.logToReport("Login attempt number " + attemptNumber);

        new LoginPage(driver).login(TestData.VALID_USERNAME, password);

        // EXPLICIT WAIT: wait for either the error box or the products title.
        TestUtils.waitForVisible(driver, By.cssSelector("[data-test='error'], .title"), 10);

        Assert.assertTrue(driver.getCurrentUrl().contains(TestData.PRODUCTS_PAGE_PART),
                "Login failed on attempt " + attemptNumber);
    }

    // 2. invocationCount
    // Runs this test 3 times. BaseTest opens a fresh browser for each run.
    @Test(groups = {"advanced"}, invocationCount = 3,
          description = "Login page title is Swag Labs, checked 3 times")
    public void loginPageTitleRepeats() throws IOException {
        TestUtils.logToReport("Starting test: loginPageTitleRepeats (one of 3 runs)");

        String title = driver.getTitle();
        TestUtils.logToReport("Login page title: " + title);
        TestUtils.saveScreenshot(driver, "loginPageTitleRepeats");

        Assert.assertEquals(title, "Swag Labs", "Login page title should be Swag Labs");
    }

    // 3. expectedExceptions
    // The test PASSES when the code throws IllegalArgumentException, because that is the correct behaviour
    // when a product does not exist. If nothing is thrown, the test fails.
    @Test(groups = {"advanced"}, expectedExceptions = IllegalArgumentException.class,
          description = "Adding a product that is not on the page throws an error (expected)")
    public void addingFakeProductThrowsError() {
        new LoginPage(driver).login(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);
        InventoryPage inventory = new InventoryPage(driver);
        inventory.waitUntilLoaded();

        inventory.addProductToCart("Fake Product That Does Not Exist");
    }

    // 4. enabled = false
    // TestNG does not run this test at all. Change it to enabled = true to turn it back on.
    @Test(enabled = false, groups = {"advanced"},
          description = "Turned off with enabled = false, so it never runs")
    public void disabledExample() {
        Assert.fail("This should never run because enabled = false");
    }

    // 5. timeOut + a delay that makes it fail on purpose.
    // The test allows 2 seconds but sleeps for 3, so TestNG fails it.
    // It has its own group, so the main run stays green.
    @Test(groups = {"timeout-demo"}, timeOut = 2000,
          description = "Sleeps 3 seconds but the limit is 2 seconds, so it fails on purpose")
    public void pageTakesTooLongToLoad() throws InterruptedException {
        new LoginPage(driver).login(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);

        Thread.sleep(3000); // the delay that makes the test fail

        Assert.assertTrue(true);
    }
}
