package com.saucedemo.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * A simple listener. TestNG calls these methods by itself while the tests run.
 * It does not change any test code. It only prints a short line at each event.
 * It is registered in testng.xml.
 */
public class SimpleListener implements ITestListener {

    private int passed = 0;
    private int failed = 0;

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[LISTENER] Test started: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passed++;
        System.out.println("[LISTENER] Test passed: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failed++;
        System.out.println("[LISTENER] Test FAILED: " + result.getName()
                + " because: " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[LISTENER] Test skipped: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("[LISTENER] Run finished. Passed: " + passed + ", Failed: " + failed);
    }
}
