package com.saucedemo.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry rule: when a test fails, TestNG asks this class "try again?".
 * Return true to run the test again, false to stop.
 * Here we allow 2 retries after the first failure (3 runs in total at most).
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 2;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRIES) {
            retryCount++;
            TestUtils.logToReport("[RETRY] " + result.getName() + " failed. Retry " + retryCount
                    + " of " + MAX_RETRIES);
            return true;
        }
        return false;
    }
}
