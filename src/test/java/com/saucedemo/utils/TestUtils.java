package com.saucedemo.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;

/**
 * Utility functions that many tests can use.
 * All methods are static, so call them as TestUtils.methodName(...).
 */
public class TestUtils {

    private static final Path SCREENSHOT_FOLDER = Paths.get("target", "screenshots");

    private TestUtils() {
        // No objects needed: this class only holds helper methods.
    }

    /**
     * IMPLICIT WAIT: the driver keeps looking for an element for up to 'seconds'
     * before it fails. It applies to every findElement call for the rest of the session.
     */
    public static void setImplicitWait(WebDriver driver, int seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }

    /**
     * EXPLICIT WAIT: wait only for one specific condition, up to 'seconds'.
     * Here we wait until the element is visible, then return it.
     */
    public static WebElement waitForVisible(WebDriver driver, By locator, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * JAVASCRIPT CLICK: clicks the element through JavaScript instead of a normal mouse click.
     * We use it because normal clicks on some Sauce Demo buttons were not registered in our runs.
     */
    public static void jsClick(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * REPORT LOG: writes the message to the console and to the TestNG report.
     * 'true' means also print it to the console.
     */
    public static void logToReport(String message) {
        Reporter.log(message, true);
    }

    /**
     * SCREENSHOT as bytes: takes a picture of the browser and returns the raw image data.
     * Use this when you want to attach the image somewhere, or save it yourself.
     */
    public static byte[] takeScreenshotBytes(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Takes a screenshot and saves it as a PNG file in target/screenshots.
     * Returns the saved file path, so you can log it.
     */
    public static Path saveScreenshot(WebDriver driver, String fileName) throws IOException {
        byte[] imageBytes = takeScreenshotBytes(driver);
        Files.createDirectories(SCREENSHOT_FOLDER);
        Path file = SCREENSHOT_FOLDER.resolve(fileName + ".png");
        Files.write(file, imageBytes);
        embedInReport(fileName, imageBytes);
        return file;
    }

    /**
     * EMBED IN REPORT: puts the picture inside the TestNG report (emailable-report.html).
     * The image is written as base64 text, so the report shows it without needing the file.
     * The picture appears in the log in the order this method is called.
     */
    private static void embedInReport(String fileName, byte[] imageBytes) {
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        Reporter.log("<p>Screenshot: " + fileName + "</p>"
                + "<img src=\"data:image/png;base64," + base64 + "\" width=\"600\"/>", false);
    }
}
