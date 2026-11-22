package com.saucedemo.pages;
import com.saucedemo.utils.TestUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object for the cart page (cart.html).
 */
public class CartPage {

    // A LIST of every item name in the cart.
    @FindBy(className = "inventory_item_name")
    private List<WebElement> itemNames;

    private final WebDriver driver;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Waits until the cart's item list is visible before we read it.
    public void waitUntilLoaded() {
        TestUtils.waitForVisible(driver, By.className("cart_list"), 10);
    }

    // Reads the name of every item in the cart.
    public List<String> getItemNames() {
        List<String> names = new ArrayList<>();
        for (WebElement nameElement : itemNames) {
            names.add(nameElement.getText());
        }
        return names;
    }
}
