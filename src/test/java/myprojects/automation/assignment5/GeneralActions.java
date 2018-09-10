package myprojects.automation.assignment5;


import myprojects.automation.assignment5.model.ProductData;
import myprojects.automation.assignment5.utils.Properties;
import myprojects.automation.assignment5.utils.logging.CustomReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.List;
import java.util.Random;

/**
 * Contains main script actions that may be used in scripts.
 */
public class GeneralActions {
    private WebDriver driver;
    private WebDriverWait wait;

    public GeneralActions(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
    }


    public ProductData openRandomProduct() {

        List<WebElement> allProducts = driver.findElements(By.cssSelector(".product-miniature.js-product-miniature"));
        Random random = new Random();
        WebElement selectedProduct = allProducts.get(random.nextInt(allProducts.size()));
        System.out.println(selectedProduct.getText());

        WebElement selectedProductClickable = selectedProduct.findElement(By.cssSelector("h1[itemprop = 'name']"));////h1[contains(@itemprop,'name')]
        selectedProductClickable.click();
        String productName = driver.findElement(By.cssSelector("h1[itemprop = 'name']")).getText();
        String productQty = driver.findElement(By.name("qty")).getAttribute("value");
        String productPrice = driver.findElement(By.cssSelector(".current-price")).findElement(By.cssSelector("span[itemprop = price]")).getAttribute("content");
        System.out.println(productName + " , " + productPrice);

        return new ProductData(productName,Integer.parseInt(convertNumbers(productQty)),Float.parseFloat(convertNumbers(productPrice)));
    }
    public Integer productQtyBeforeOrdering() throws NumberFormatException {
        driver.findElement(By.partialLinkText("Подробнее")).click();
        WebElement productTargetQty = driver.findElement(By.xpath("//div[contains(@class,'product-quantities')]/span"));
        String productTargetQtyString = convertNumbers(productTargetQty.getAttribute("innerHTML").trim());
        System.out.println(productTargetQtyString);
        return Integer.parseInt(productTargetQtyString);
    }
    public ProductData saveProductParameters () {
        WebElement addToCart = driver.findElement(By.cssSelector(".btn.btn-primary.add-to-cart"));
        addToCart.click();

        wait = new WebDriverWait(driver, 20);
        By submitDialog = By.cssSelector(".modal-dialog");
        WebElement submitDialogElement = driver.findElement(submitDialog);
        WebElement goButton = submitDialogElement.findElement(By.xpath("//a[contains(text(),'перейти')]"));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'перейти')]")));
        goButton.click();

        String productNameCart = driver.findElements(By.cssSelector(".product-line-info")).get(0).findElement(By.tagName("a")).getText();
        String productPriceCart = driver.findElements(By.cssSelector(".product-line-info")).get(1).findElement(By.tagName("span")).getText();
        String productQtyCart = driver.findElement(By.cssSelector(".js-cart-line-product-quantity.form-control")).getAttribute("value");
        System.out.println(productNameCart + " " + productQtyCart + " " + productPriceCart);

        return new ProductData(productNameCart,Integer.parseInt(convertNumbers(productQtyCart)),Float.parseFloat(convertNumbers(productPriceCart)));
    }


    public void fillRequiredInformation() {
        WebElement orderingButton = driver.findElement(By.xpath("//a[contains(text(),'Оформление')]"));
        orderingButton.click();
        WebElement firstName = driver.findElement(By.name("firstname"));
        firstName.sendKeys("Grigory");
        WebElement lastName = driver.findElement(By.name("lastname"));
        lastName.sendKeys("Petrenko");
        WebElement email = driver.findElement(By.name("email"));
        email.sendKeys("email" + System.currentTimeMillis() + "@gmail.com");
        WebElement buttonContinue = driver.findElement(By.cssSelector(".continue"));
        buttonContinue.click();
        WebElement address = driver.findElement(By.name("address1"));
        address.sendKeys("Poltava,Nebesnoy Sotni");
        WebElement postalIndex = driver.findElement(By.name("postcode"));
        postalIndex.sendKeys("36022");
        WebElement city = driver.findElement(By.name("city"));
        city.sendKeys("Poltava");
        WebElement buttonConfirmAdress = driver.findElement(By.name("confirm-addresses"));
        buttonConfirmAdress.click();
        WebElement buttonConfirmDeliveryOptions = driver.findElement(By.name("confirmDeliveryOption"));
        buttonConfirmDeliveryOptions.click();
        WebElement paymentOption = driver.findElement(By.id("payment-option-2"));
        paymentOption.click();
        WebElement conditionToApprove = driver.findElement(By.id("conditions_to_approve[terms-and-conditions]"));
        conditionToApprove.click();
        WebElement approve = driver.findElement(By.cssSelector(".btn.btn-primary.center-block"));
        approve.click();
    }
    public ProductData validateOrderSummary() {
        String productNameConfirmedRaw = driver.findElement(By.cssSelector(".details")).getText();
        String productNameConfirmed;
        if (productNameConfirmedRaw.lastIndexOf("- Size") != -1)
            productNameConfirmed = productNameConfirmedRaw.substring(0, productNameConfirmedRaw.lastIndexOf("- Size"));
        else
            productNameConfirmed = productNameConfirmedRaw;
        String productQtyConfirmed = driver.findElement(By.cssSelector(".qty")).findElement(By.cssSelector(".col-xs-2")).getText();
        String productPriceConfirmed = driver.findElement(By.cssSelector(".qty")).findElement(By.cssSelector(".col-xs-5.text-sm-right.text-xs-left")).getText();
        System.out.println(productNameConfirmed + " " + productQtyConfirmed + " " + productPriceConfirmed);
        return new ProductData(productNameConfirmed,Integer.parseInt(convertNumbers(productQtyConfirmed)),Float.parseFloat(convertNumbers(productPriceConfirmed)));
    }
    public int checkUpdatedInStockValue(ProductData productData) {
        driver.get(Properties.getBaseUrl());
        List<WebElement> allProductsNames = driver.findElements(By.cssSelector(".product-miniature.js-product-miniature>div>div>h1>a"));

        WebElement selectedProduct = null;
        try {
            selectedProduct = null;
            for(WebElement product : allProductsNames) {
                if (product.getText().trim().toLowerCase().equals(productData.getName().trim().toLowerCase())) {
                    selectedProduct = product;
                    break;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        System.out.println("clickable " + selectedProduct.getText());
//        selectedProduct.findElement(By.cssSelector("h1[itemprop = 'name']")).click();
        selectedProduct.click();
        driver.findElement(By.partialLinkText("Подробнее о товаре")).click();
        WebElement productTargetQty = driver.findElement(By.xpath("//div[contains(@class,'product-quantities')]/span"));

        return Integer.parseInt(convertNumbers(productTargetQty.getAttribute("innerHTML")));
    }

    public void waitForContentLoad(By somethingLocator) {
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(somethingLocator));
    }

    public String convertNumbers(String inputNumberInStrFormat) {
        String outputNumberInStrFormat = "";
        for(int i = 0; i < inputNumberInStrFormat.length(); i++) {
            if(inputNumberInStrFormat.charAt(i) == 44) {
                outputNumberInStrFormat += ".";
            } else if((inputNumberInStrFormat.charAt(i) >= 48 && inputNumberInStrFormat.charAt(i) <= 57)  || inputNumberInStrFormat.charAt(i) == 46 ) {
                outputNumberInStrFormat += inputNumberInStrFormat.charAt(i);
            }

        }
        return outputNumberInStrFormat;
    }
}
