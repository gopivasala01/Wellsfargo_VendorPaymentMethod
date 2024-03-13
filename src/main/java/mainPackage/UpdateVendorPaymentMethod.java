package mainPackage;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;


public class UpdateVendorPaymentMethod {
	
	public static boolean updateVendorPayment(WebDriver driver,String vendorPaymentMethod) {
		String failedReason = "";
		Actions actions = new Actions(driver);
		try {
			if(vendorPaymentMethod == null) {
				vendorPaymentMethod = "VCC";
			}
			if(vendorPaymentMethod.contains("ACH")) {
				vendorPaymentMethod = "ACH Epay";
			}
			System.out.println("Vendor payment Method is = "+vendorPaymentMethod);
			driver.findElement(Locators.edtVendor).click();
			Thread.sleep(2000);
			actions.moveToElement(driver.findElement(Locators.vendorPaymentMethodDropDown)).build().perform();
			Select select = new Select(driver.findElement(Locators.vendorPaymentMethodDropDown));
			select.selectByVisibleText(vendorPaymentMethod);
			
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
