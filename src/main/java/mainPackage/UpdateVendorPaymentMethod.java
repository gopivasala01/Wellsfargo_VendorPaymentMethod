package mainPackage;


import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;


public class UpdateVendorPaymentMethod {
	
	public static boolean updateVendorPayment(WebDriver driver,String vendorPaymentMethod, String email) {
		String failedReason = "";
		Actions actions = new Actions(driver);
		try {
			driver.findElement(Locators.edtVendor).click();
			if(vendorPaymentMethod.equalsIgnoreCase("Enrolled") || vendorPaymentMethod.equalsIgnoreCase("Follow Up - Enrolled")) {
				vendorPaymentMethod = "VCC";
			}
			else if(vendorPaymentMethod.contains("Verified")) {
				vendorPaymentMethod = "ACH Epay";
			}
			else {
				vendorPaymentMethod = "Check";
			}
			System.out.println("Vendor payment Method is from DB = "+vendorPaymentMethod);
			Thread.sleep(2000);
			//actions.moveToElement(driver.findElement(Locators.email)).build().perform();
			driver.findElement(Locators.email).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
			driver.findElement(Locators.email).sendKeys(email);
			actions.moveToElement(driver.findElement(Locators.vendorPaymentMethodDropDown)).build().perform();
			Select select = new Select(driver.findElement(Locators.vendorPaymentMethodDropDown));
			WebElement defaultElement = select.getFirstSelectedOption();
			String defaultValue = defaultElement.getText();
			System.out.println("Default Vendor payment Method in PW = "+defaultValue);
			if(!defaultValue.contains(vendorPaymentMethod)) {
				select.selectByVisibleText(vendorPaymentMethod);

			}
						
			if (AppConfig.saveButtonOnAndOff == false) {
				actions.moveToElement(driver.findElement(Locators.cancelPayment)).build().perform();
				driver.findElement(Locators.cancelPayment).click();
				return true;
			} else {
				actions.moveToElement(driver.findElement(Locators.savePayment)).build().perform();
				driver.findElement(Locators.savePayment).click();
				PropertyWare.evictionPopUp(driver);
				Thread.sleep(2000);
				try {
					driver.switchTo().alert().accept();
					failedReason = "";
					//System.out.println("Updated Check Number = " + updatedCheckNumberFieldValue);
					RunnerClass.setFailedReason(failedReason);
				} catch (Exception e) {
				}

				try {
					if (driver.findElement(Locators.errorMessage).isDisplayed()) {
						failedReason = failedReason + "There is an error while saving Payment Method";
						//System.out.println("Check Number = " + updatedCheckNumberFieldValue);
						RunnerClass.setFailedReason(failedReason);
						return false;
					}
				} catch (Exception e) {
				}
				return true;
			}

		} catch (Exception e) {
			failedReason = failedReason + "Payment Method could not be saved";
			RunnerClass.setFailedReason(failedReason);
			return false;
		}
	}

}
