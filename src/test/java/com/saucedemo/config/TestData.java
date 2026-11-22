package com.saucedemo.config;

/**
 * All test values live here, in one place.
 * Change a username, password or message here and every test that uses it updates.
 */
public class TestData {

    // A user that can log in successfully.
    public static final String VALID_USERNAME = "standard_user";
    public static final String VALID_PASSWORD = "secret_sauce";

    // A user that is blocked.
    public static final String LOCKED_USERNAME = "locked_out_user";
    public static final String LOCKED_PASSWORD = "secret_sauce";

    // The error message the site shows for the locked user.
    public static final String LOCKED_ERROR_MESSAGE =
            "Epic sadface: Sorry, this user has been locked out.";

    // Part of the URL that the products page always has after a good login.
    public static final String PRODUCTS_PAGE_PART = "inventory.html";

    // What the products page should show.
    public static final String PRODUCTS_TITLE = "Products";
    public static final int PRODUCT_COUNT = 6;
    public static final String BACKPACK_NAME = "Sauce Labs Backpack";
}
