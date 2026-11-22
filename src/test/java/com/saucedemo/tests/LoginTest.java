package com.saucedemo.tests;
import com.saucedemo.base.BaseTest;
import com.saucedemo.config.TestData;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.TestUtils;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Login tests.
 * BaseTest opens the browser before each test and closes it after each test.
 * The usernames, passwords and messages come from TestData.
 * TestUtils gives us logging, explicit waits and screenshots.
 */
public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"},
          description = "Valid user logs in and reaches the products page")
    public void validUserCanLogin() throws IOException {
        TestUtils.logToReport("Starting test: validUserCanLogin");
        LoginPage loginPage = new LoginPage(driver);

        // EXPLICIT WAIT: wait up to 10 seconds for the login button to be visible.
        TestUtils.waitForVisible(driver, By.id("login-button"), 10);

        loginPage.login(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);

        // Screenshot taken before the asserts, so we have a picture even if an assert fails.
        TestUtils.saveScreenshot(driver, "validUserCanLogin");
        TestUtils.logToReport("Current URL: " + driver.getCurrentUrl());

        Assert.assertTrue(driver.getCurrentUrl().contains(TestData.PRODUCTS_PAGE_PART),
                "User should land on the products page");
    }

    @Test(groups = {"negative", "regression"},
          description = "Locked-out user sees the locked-out error message")
    public void lockedOutUserSeesErrorMessage() throws IOException {
        TestUtils.logToReport("Starting test: lockedOutUserSeesErrorMessage");
        LoginPage loginPage = new LoginPage(driver);

        // EXPLICIT WAIT: wait up to 10 seconds for the login button to be visible.
        TestUtils.waitForVisible(driver, By.id("login-button"), 10);

        loginPage.login(TestData.LOCKED_USERNAME, TestData.LOCKED_PASSWORD);

        // EXPLICIT WAIT: wait for the error box to appear before reading it.
        TestUtils.waitForVisible(driver, By.cssSelector("[data-test='error']"), 10);
        TestUtils.saveScreenshot(driver, "lockedOutUserSeesErrorMessage");

        String actualError = loginPage.getErrorMessage();
        TestUtils.logToReport("Error shown: " + actualError);

        Assert.assertEquals(actualError, TestData.LOCKED_ERROR_MESSAGE);
    }
}
