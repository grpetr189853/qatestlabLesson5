package myprojects.automation.assignment5.tests;

import myprojects.automation.assignment5.BaseTest;
import myprojects.automation.assignment5.model.ProductData;
import myprojects.automation.assignment5.utils.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PlaceOrderTest extends BaseTest {

    @Test
    public void checkSiteVersion() {
        // TODO open main page and validate website version
        driver.get(Properties.getBaseUrl());
        WebDriverWait wait = new WebDriverWait(driver, 30);

        if ( driver.findElement(By.id("_desktop_logo")).isDisplayed() &&
             driver.findElement(By.id("_desktop_top_menu")).isDisplayed() &&
             driver.findElement(By.id("_desktop_contact_link")).isDisplayed() && driver.findElement(By.id("_desktop_language_selector")).isDisplayed() &&
             driver.findElement(By.id("_desktop_currency_selector")).isDisplayed() &&
             driver.findElement(By.id("_desktop_user_info")).isDisplayed() &&
             driver.findElement(By.id("_desktop_cart")).isDisplayed() &&
             driver.findElement(By.id("carousel")).isDisplayed()) {
            System.out.println("Desktop version is displayed");
        } else if (driver.findElement(By.id("_mobile_cart")).isDisplayed()&&
             driver.findElement(By.id("_mobile_cart")).isDisplayed() &&
             driver.findElement(By.id("_mobile_user_info")).isDisplayed() &&
             driver.findElement(By.id("_mobile_logo")).isDisplayed() &&
             driver.findElement(By.id("menu-icon")).isDisplayed()) {
            System.out.println("Mobile version is displayed");
        }


    }

    @Test
    public void createNewOrder() {
        driver.get(Properties.getBaseUrl());
        WebDriverWait wait = new WebDriverWait(driver, 30);
        List<WebElement> allGoods = driver.findElements(By.className("all-product-link"));
        WebElement goods = allGoods.get(0);
        goods.click();

        // TODO implement order creation test

        // open random product
        ProductData productFromProductPage = actions.openRandomProduct();
        // save product parameters
        int productQtyBeforeOrdering = actions.productQtyBeforeOrdering();
        ProductData productFromUserscart = actions.saveProductParameters();

        // add product to Cart and validate product information in the Cart
        Assert.assertEquals(productFromProductPage.getName().trim().toLowerCase(),productFromUserscart.getName().trim().toLowerCase());
        Assert.assertEquals(productFromProductPage.getQty(),productFromUserscart.getQty());
        Assert.assertEquals(Math.round(productFromProductPage.getPrice()*100)/100,Math.round(productFromUserscart.getPrice()*100)/100);
        // proceed to order creation, fill required information
        actions.fillRequiredInformation();

        // place new order and validate order summary
        ProductData productConfirmed = actions.validateOrderSummary();
        Assert.assertEquals(productConfirmed.getName().trim().toLowerCase(),productFromProductPage.getName().trim().toLowerCase());
        Assert.assertEquals(productConfirmed.getQty(),productFromProductPage.getQty());
        Assert.assertEquals(Math.round(productConfirmed.getPrice()*100)/100,Math.round(productFromProductPage.getPrice()*100)/100);
        // check updated In Stock value
        int productQtyAfterOrdering = actions.checkUpdatedInStockValue(productConfirmed);
        Assert.assertEquals(productQtyBeforeOrdering - 1 , productQtyAfterOrdering);
    }

}
