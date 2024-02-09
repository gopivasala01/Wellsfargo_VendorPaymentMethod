package mainPackage;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class PropertyWare
{
	
	
	public static boolean selectLease(WebDriver driver,String company, String leaseEntityID)
	{
		String failedReason = "";
		try
		{
			driver.manage().timeouts().implicitlyWait(100,TimeUnit.SECONDS);
	        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(100));
	        driver.navigate().refresh();
	        PropertyWare.intermittentPopUp(driver);
	        //if(PropertyWare.checkIfBuildingIsDeactivated()==true)
	        	//return false;
	        //if(RunnerClass.previousRecordCompany==null||!RunnerClass.previousRecordCompany.equals(company)||RunnerClass.previousRecordCompany.equals("") || RunnerClass.loggedOut == true)
	        {
	        driver.findElement(Locators.marketDropdown).click();
	        String marketName = "HomeRiver Group - "+company;
	        Select marketDropdownList = new Select(driver.findElement(Locators.marketDropdown));
	        marketDropdownList.selectByVisibleText(marketName);
	        Thread.sleep(3000);
	        }
	        String buildingPageURL = AppConfig.buildingPageURL+ leaseEntityID;
	        driver.navigate().to(buildingPageURL);
	        if(PropertyWare.permissionDeniedPage(driver)==true)
	        {
	        	System.out.println("Wrong Lease Entity ID");
	        	failedReason = "Wrong Lease Entity ID";
	        	RunnerClass.setFailedReason(failedReason);
	        	return false;
	        }
	        PropertyWare.intermittentPopUp(driver);
	        if(PropertyWare.checkIfBuildingIsDeactivated(driver)==true)
	        {
	        	return false;
	        }
	        String portfolioName = driver.findElement(Locators.portfolioName).getText();
	        RunnerClass.setPortfolioName(portfolioName);
	        String status = driver.findElement(Locators.status).getText();
	        if(status.equalsIgnoreCase("ACTIVE") || status.equalsIgnoreCase("Active - Month to Month") || status.equalsIgnoreCase("Active - TTO") || status.equalsIgnoreCase("Active - Notice Given")){
	        	System.out.println("Status = " + status);
	        	//RunnerClass.failedReason = "Lease is Active";
	        	return true;
	        }
	        else {
	        	System.out.println("Status = " + status);
	        	failedReason = "Lease is not Active";
	        	RunnerClass.setFailedReason(failedReason);
	        	return false;
	        }
		}
		catch(Exception e)
		{
			failedReason= "Lease not found";
			RunnerClass.setFailedReason(failedReason);
			return false;
		}
	}
	public static void intermittentPopUp(WebDriver driver)
	{
		//Pop up after clicking lease name
				try
				{
					driver.manage().timeouts().implicitlyWait(1,TimeUnit.SECONDS);
			        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(1));
			        try {
			        	Thread.sleep(1000);
			        	driver.switchTo().frame(driver.findElement(Locators.scheduleMaintananceIFrame));
			        	if(driver.findElement(Locators.scheduleMaintanancePopUp2).isDisplayed()) {
			        		Thread.sleep(1000);
			        		driver.findElement(Locators.maintananceCloseButton).click();
			        	}
			        	driver.switchTo().defaultContent();
			        }
			        catch(Exception e) {}
			        try
			        {
					if(driver.findElement(Locators.popUpAfterClickingLeaseName).isDisplayed())
					{
						driver.findElement(Locators.popupClose).click();
					}
			        }
			        catch(Exception e) {}
			        try
			        {
					if(driver.findElement(Locators.scheduledMaintanancePopUp).isDisplayed())
					{
						driver.findElement(Locators.scheduledMaintanancePopUpOkButton).click();
					}
			        }
			        catch(Exception e) {}
			        try
			        {
			        if(driver.findElement(Locators.scheduledMaintanancePopUpOkButton).isDisplayed())
			        	driver.findElement(Locators.scheduledMaintanancePopUpOkButton).click();
			        }
			        catch(Exception e) {}
					driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
			        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
				}
				catch(Exception e) {}
			
	}
	public static void evictionPopUp(WebDriver driver)
	{
		//Pop up after clicking lease name
				try
				{
					driver.manage().timeouts().implicitlyWait(1,TimeUnit.SECONDS);
			        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(1));
			        try
			        {
					if(driver.findElement(Locators.evictionPopUp).isDisplayed())
					{
						try
						{
							if(driver.findElement(Locators.evictionNotAcceptPaymentCheckbox).isSelected()&& driver.findElement(Locators.evictionNotAllowportalCheckbox).isSelected()) {
								driver.findElement(Locators.saveEvictionPopUp).click();
							}
						}
						catch(Exception e) {}
					}
			        }
			        catch(Exception e) {}
					driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
			        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
				}
				catch(Exception e) {}
			
	}
	
	public static boolean checkIfBuildingIsDeactivated(WebDriver driver)
	{
		//Pop up after clicking lease name
		String failedReason = "";
				try
				{
					driver.manage().timeouts().implicitlyWait(1,TimeUnit.SECONDS);
			        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(1));
			        try
			        {
					if(driver.findElement(Locators.buildingDeactivatedMessage).isDisplayed())
					{
						System.out.println("Building is Deactivated");
						failedReason = "Building is Deactivated";
						RunnerClass.setFailedReason(failedReason);
						driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
				        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			        	return true;
					}
			        }
			        catch(Exception e) {}
			        
					driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
			        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			        return false;
				}
				catch(Exception e) {}
				return false;
				
	}
	public static boolean permissionDeniedPage(WebDriver driver)
	{
		try
		{
		driver.manage().timeouts().implicitlyWait(1,TimeUnit.SECONDS);
        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        if(driver.findElement(Locators.permissionDenied).isDisplayed())
        {
        	driver.navigate().back();
        	return true;
        }
        driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		}
		catch(Exception e)
		{
			return false;
		}
		return false;
	}

}
