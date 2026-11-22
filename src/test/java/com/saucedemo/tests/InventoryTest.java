package com.saucedemo.tests;
import com.saucedemo.base.BaseTest;
import com.saucedemo.config.TestData;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.TestUtils;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

/**
 * Tests for the products page.
 * BaseTest opens Chrome first. Then @BeforeMethod below logs in,
 * so every test starts on the products page.
 * TestUtils gives us logging, explicit waits and screenshots.
 *
 * Order: priority 1 runs first, then 2, 3, 4.
 * The cart test also depends on pageShowsSixProducts passing.
 */
public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void logInFirst() {
        new LoginPage(driver).login(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);
        inventoryPage = new InventoryPage(driver);
        inventoryPage.waitUntilLoaded();
    }

    // DATA PROVIDER: supplies the data for a test. Each row is one run of the test.
    // Here there is one row, and the row holds the list of products to add to the cart.
    @DataProvider(name = "productsToAdd")
    public Object[][] productsToAdd() {
        return new Object[][] {
                { List.of("Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Onesie") }
        };
    }

    @Test(groups = {"smoke", "regression"}, priority = 1,
          description = "Products page has the title Products and the inventory URL")
    public void pageHasCorrectTitleAndUrl() throws IOException {
        TestUtils.logToReport("Starting test: pageHasCorrectTitleAndUrl");

        // EXPLICIT WAIT: wait up to 10 seconds for the page title to be visible.
        TestUtils.waitForVisible(driver, By.className("title"), 10);
        TestUtils.saveScreenshot(driver, "pageHasCorrectTitleAndUrl");

        String title = inventoryPage.getTitleText();
        TestUtils.logToReport("Page title: " + title);

        Assert.assertEquals(title, TestData.PRODUCTS_TITLE, "Page title should be 'Products'");
        Assert.assertTrue(driver.getCurrentUrl().contains(TestData.PRODUCTS_PAGE_PART),
                "URL should point to the products page");
    }

    @Test(groups = {"smoke", "regression"}, priority = 2,
          description = "Products page shows six products")
    public void pageShowsSixProducts() throws IOException {
        TestUtils.logToReport("Starting test: pageShowsSixProducts");

        // EXPLICIT WAIT: wait up to 10 seconds for the first product to be visible.
        TestUtils.waitForVisible(driver, By.className("inventory_item"), 10);
        TestUtils.saveScreenshot(driver, "pageShowsSixProducts");

        int count = inventoryPage.getProductCount();
        TestUtils.logToReport("Products found: " + count);

        Assert.assertEquals(count, TestData.PRODUCT_COUNT, "Products page should show six products");
    }

    @Test(groups = {"regression"}, priority = 3,
          description = "Product list contains Sauce Labs Backpack")
    public void productListContainsBackpack() throws IOException {
        TestUtils.logToReport("Starting test: productListContainsBackpack");

        // EXPLICIT WAIT: wait up to 10 seconds for the product names to be visible.
        TestUtils.waitForVisible(driver, By.className("inventory_item_name"), 10);
        TestUtils.saveScreenshot(driver, "productListContainsBackpack");

        List<String> names = inventoryPage.getProductNames();
        TestUtils.logToReport("Product names: " + names);

        Assert.assertFalse(names.isEmpty(), "Product list should not be empty");
        Assert.assertTrue(names.contains(TestData.BACKPACK_NAME),
                "Product list should contain " + TestData.BACKPACK_NAME);
    }

    @Test(dataProvider = "productsToAdd", groups = {"regression"}, priority = 4,
          dependsOnMethods = {"pageShowsSixProducts"},
          description = "Add the listed products to the cart, open the cart, and check they are there")
    public void addProductsToCartAndCheckCart(List<String> productsToAdd) throws IOException {
        TestUtils.logToReport("Starting test: addProductsToCartAndCheckCart with " + productsToAdd);

        // Add each product from the data provider list.
        for (String product : productsToAdd) {
            inventoryPage.addProductToCart(product);
        }

        // EXPLICIT WAIT: the cart icon number should match how many we added.
        int badgeCount = inventoryPage.waitForCartBadgeCount(productsToAdd.size());
        TestUtils.saveScreenshot(driver, "addProductsToCart");
        Assert.assertEquals(badgeCount, productsToAdd.size(), "Cart icon should show the number of items added");

        // Open the cart and read what is in it.
        inventoryPage.openCart();
        CartPage cartPage = new CartPage(driver);
        cartPage.waitUntilLoaded();
        TestUtils.saveScreenshot(driver, "cartContents");

        List<String> itemsInCart = cartPage.getItemNames();
        TestUtils.logToReport("Items in cart: " + itemsInCart);

        Assert.assertTrue(driver.getCurrentUrl().contains("cart.html"), "Should be on the cart page");
        Assert.assertEquals(itemsInCart.size(), productsToAdd.size(), "Cart should hold the same number of items");
        Assert.assertTrue(itemsInCart.containsAll(productsToAdd), "Cart should contain every product added");
    }
}
