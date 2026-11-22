package com.saucedemo.pages;
import com.saucedemo.utils.TestUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page object for the products page (inventory.html).
 *
 * @FindBy tells PageFactory how to find each element.
 * PageFactory.initElements(...) in the constructor fills them in.
 */
public class InventoryPage {

    // ONE element: @FindBy finds the first match.
    @FindBy(className = "title")
    private WebElement pageTitle;

    // A LIST of elements: @FindBy finds ALL matches, so use List<WebElement>.
    @FindBy(className = "inventory_item")
    private List<WebElement> productItems;

    // Another list: the name text of every product.
    @FindBy(className = "inventory_item_name")
    private List<WebElement> productNames;

    // The cart icon in the top right corner.
    @FindBy(className = "shopping_cart_link")
    private WebElement cartLink;

    private final WebDriver driver;

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Waits until the page title is shown before we read anything.
    public void waitUntilLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public String getTitleText() {
        return pageTitle.getText();
    }

    // Number of products on the page.
    public int getProductCount() {
        return productItems.size();
    }

    // Reads the text of every product name into a normal Java list.
    public List<String> getProductNames() {
        List<String> names = new ArrayList<>();
        for (WebElement nameElement : productNames) {
            names.add(nameElement.getText());
        }
        return names;
    }

    // Finds the product with this name, then clicks its "Add to cart" button.
    public void addProductToCart(String productName) {
        for (WebElement item : productItems) {
            String name = item.findElement(By.className("inventory_item_name")).getText();
            if (name.equals(productName)) {
                WebElement addButton = item.findElement(By.tagName("button"));
                TestUtils.jsClick(driver, addButton);
                return;
            }
        }
        throw new IllegalArgumentException("Product not found on the page: " + productName);
    }

    // The number shown on the cart icon. Returns 0 when the icon has no number.
    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(By.className("shopping_cart_badge"));
        if (badges.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(badges.get(0).getText());
    }

    // Waits until the cart icon shows the expected number, then returns the number.
    public int waitForCartBadgeCount(int expected) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> getCartBadgeCount() == expected);
        } catch (org.openqa.selenium.TimeoutException e) {
            // Fall through: the test's assert will show the real number.
        }
        return getCartBadgeCount();
    }

    // Opens the cart page.
    public void openCart() {
        TestUtils.jsClick(driver, cartLink);
    }
}
