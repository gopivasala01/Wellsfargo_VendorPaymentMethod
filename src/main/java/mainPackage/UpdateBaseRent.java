package mainPackage;


import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UpdateBaseRent 
{
	public static boolean getBaseRentAmount(WebDriver driver,String company,String dateDifference,String moveInDate ) throws Exception
	{
		String failedReason = "";
		Actions actions = new Actions(driver);
		JavascriptExecutor js = (JavascriptExecutor)driver;
		try
		{
		driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
        RunnerClass.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.findElement(Locators.summaryEditButton).click();
        Thread.sleep(2000);
        String BaseRentFieldValueFromPW = driver.findElement(Locators.BaseRentFieldinPW).getAttribute("value").replace("$", "");
        String baseRentFromPW = BaseRentFieldValueFromPW;
        try {
        	 RunnerClass.setBaseRentFromPW(baseRentFromPW);
        }
       catch(Exception e){
    	   System.out.println("Unable to parse baseRentFromPW");
       }
        System.out.println("Base rent in PW field - " + baseRentFromPW);
        Thread.sleep(2000);
		js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
		actions.moveToElement(driver.findElement(Locators.newAutoCharge)).build().perform();
 		List<WebElement> autoChargeCodes = driver.findElements(Locators.autoCharge_List);
		List<WebElement> autoChargeAmounts = driver.findElements(Locators.autoCharge_Amount);
		List<WebElement> autoChargeStartDates = driver.findElements(Locators.autoCharge_StartDate);
		List<WebElement> autoChargeEndDates = driver.findElements(Locators.autoCharge_EndDate);
		List<WebElement> autoChargeDescriptions = driver.findElements(Locators.autoCharge_description);
		
		try
		{
			if(autoChargeCodes.size()==1&&autoChargeCodes.get(0).getText().equals("No Charges"))
			{
				System.out.println("No Auto Charges");
				RunnerClass.setBaseRentAmount("");
				failedReason = "No Auto Charges";
				RunnerClass.setFailedReason(failedReason);
				return false;
			}
				
		}
		catch(Exception e)
		{
			
		}
		
		String dateCalculated=null;
		boolean baseRentAvailable = false;
		boolean subsidizedRentAvailable = false;
		if(dateDifference.equals("")){
			failedReason = "No Move In date";
			return false;
		}
		int days = Integer.parseInt(dateDifference);
		if(days<=62)
			dateCalculated = CommonMethods.getCalculatedDate(moveInDate);
		else 
			dateCalculated =CommonMethods.getCurrentDate();  
		System.out.println(dateCalculated.toString());
		double rentCalculated =0.00d;
		double subsidizedRentCalculated =0.00d;
		for(int i=0;i<autoChargeCodes.size();i++)
		{
			String autoChargeCode = autoChargeCodes.get(i).getText().split("-")[0].trim();
			if(AppConfig.getSubsidizedMonthlyRentChargeCode(company).contains(autoChargeCode)) {
				String subsidizedStartDate = autoChargeStartDates.get(i).getText();
				String subsidizedChargeEndDate = autoChargeEndDates.get(i).getText();
				String subsidizedChargeAmount = autoChargeAmounts.get(i).getText();
				if(CommonMethods.compareDates(subsidizedStartDate,dateCalculated)==true&&((subsidizedChargeEndDate.trim().equals(""))||CommonMethods.compareDates(dateCalculated, subsidizedChargeEndDate))&&!subsidizedChargeAmount.contains("-$")) {
					String subsidizedRent =  autoChargeAmounts.get(i).getText();
					double d =Double.parseDouble(subsidizedRent.substring(1, subsidizedRent.length()).replace(",", ""));
					subsidizedRentCalculated = d + subsidizedRentCalculated;
					subsidizedRentCalculated = Math.round(subsidizedRentCalculated * 100.0) / 100.0;
					subsidizedRentAvailable = true;
				}
				
			}
			
			if(AppConfig.getMonthlyRentChargeCode(company).contains(autoChargeCode))
			{
				String autoChargeStartDate = autoChargeStartDates.get(i).getText();
				String autoChargeEndDate = autoChargeEndDates.get(i).getText();
				String autoChargeAmount = autoChargeAmounts.get(i).getText();
				String autoChargeDescription = autoChargeDescriptions.get(i).getText();
				if(CommonMethods.compareDates(autoChargeStartDate,dateCalculated)==true&&((autoChargeEndDate.trim().equals(""))||CommonMethods.compareDates(dateCalculated, autoChargeEndDate))&&!autoChargeAmount.contains("-$"))
				{
					if(autoChargeDescription.toLowerCase().contains("mtm") || autoChargeDescription.toLowerCase().contains("pet")) {
						continue;
					}
					else {
						String baseRent =  autoChargeAmounts.get(i).getText();
						double d =Double.parseDouble(baseRent.substring(1, baseRent.length()).replace(",", ""));
						rentCalculated = Double.sum(d, rentCalculated);
						rentCalculated = Math.round(rentCalculated * 100.0) / 100.0;
						baseRentAvailable = true;
					}
					
					//break;
				}
			}
		}
		String baseRentAmount="";
		if(baseRentAvailable==true)
		{
			if(subsidizedRentAvailable == true) {
				baseRentAmount =String.valueOf(rentCalculated+subsidizedRentCalculated);
				 try {
					 RunnerClass.setBaseRentAmount(baseRentAmount);
		        }
		       catch(Exception e){
		    	   System.out.println("Unable to parse baseRentAmount");
		       }
				
				if(baseRentAmount.endsWith(".0"))
				{
					baseRentAmount= baseRentAmount+"0";
					 try {
						 RunnerClass.setBaseRentAmount(baseRentAmount);
			        }
			       catch(Exception e){
			    	   System.out.println("Unable to parse baseRentAmount");
			       }
					System.out.println(baseRentAmount+" is the Base Rent");
					//updateBaseRent();
				}
				
			}
			else {
				baseRentAmount =String.valueOf(rentCalculated);
				 try {
					 RunnerClass.setBaseRentAmount(baseRentAmount);
		        }
		       catch(Exception e){
		    	   System.out.println("Unable to parse baseRentAmount");
		       }
				if(baseRentAmount.endsWith(".0"))
				{
					baseRentAmount= baseRentAmount+"0";
					 try {
						 RunnerClass.setBaseRentAmount(baseRentAmount);
			        }
			       catch(Exception e){
			    	   System.out.println("Unable to parse baseRentAmount");
			       }
					System.out.println(baseRentAmount+" is the Base Rent");
					//updateBaseRent();
				}
			}
			
			
		}
		if(baseRentAvailable == false) 
		{
			System.out.println("Base Rent is not available");
			failedReason = "Base Rent Not Available";
			RunnerClass.setFailedReason(failedReason);
			//RunnerClass.baseRentAmount = "Not Found";
			return false;
		}
		
		return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			failedReason = "Could not find Base Rent";
			RunnerClass.setFailedReason(failedReason);
			return false;
		}
	}
	
	public static boolean updateBaseRent(WebDriver driver)
	{
		String failedReason = "";
		Actions actions = new Actions(driver);
		try
		{
			actions.moveToElement(driver.findElement(Locators.baseRent)).build().perform();
			if(driver.findElement(Locators.baseRent).getAttribute("value").replace("$", "").replace(",", "").split(Pattern.quote(".")).length != 0){
				if( driver.findElement(Locators.baseRent).getAttribute("value").replace("$", "").replace(",", "").split(Pattern.quote("."))[0].equals((RunnerClass.getBaseRentAmount()).split(Pattern.quote("."))[0]))
				{
					failedReason = "Base Rent Already Exists";
					RunnerClass.setFailedReason(failedReason);
					return true;
				}
				else
				{
					driver.findElement(Locators.baseRent).sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
				    driver.findElement(Locators.baseRent).sendKeys(RunnerClass.getBaseRentAmount());  
				    String UpdatedBaseRentFieldValue = driver.findElement(Locators.baseRent).getAttribute("value").replace("$", "");
			        //RunnerClass.baseRentFromPW = UpdatedBaseRentFieldValue;
				    System.out.println("Updated Base rent in PW field - " + UpdatedBaseRentFieldValue);
				    	 if(AppConfig.saveButtonOnAndOff==false)
				 		{
				 			 actions.moveToElement(driver.findElement(Locators.cancelLease)).build().perform();
				 			 driver.findElement(Locators.cancelLease).click();
				 			 return true;
				 		}
				 	    else 
				 	    {
				 			 actions.moveToElement(driver.findElement(Locators.saveLease)).build().perform();
				 			 driver.findElement(Locators.saveLease).click();
				 			 PropertyWare.evictionPopUp(driver);
				 			 Thread.sleep(2000);
				 			 try
				 			 {
				 				driver.switchTo().alert().accept();
				 				failedReason = failedReason + "";
				 				RunnerClass.setFailedReason(failedReason);
				 			 }
				 			 catch(Exception e) {}
				 			 
				 			 try
				 			 {
				 			 if(driver.findElement(Locators.saveLease).isDisplayed())
				 			 {
				 				 failedReason = failedReason + "Base Rent could not be saved";
				 				RunnerClass.setFailedReason(failedReason);
				 				 return false;
				 			 }
				 			 }
				 			 catch(Exception e) {}
				 			 return true;
				 	    }
				    	
				}
				
			}
	
			else {
				
				driver.findElement(Locators.baseRent).sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
			    driver.findElement(Locators.baseRent).sendKeys(RunnerClass.getBaseRentAmount());
			    String UpdatedBaseRentFieldValue = driver.findElement(Locators.baseRent).getAttribute("value").replace("$", "");
		        //RunnerClass.baseRentFromPW = UpdatedBaseRentFieldValue;
		       System.out.println("Updated Base rent in PW field - " + UpdatedBaseRentFieldValue);
			    	 if(AppConfig.saveButtonOnAndOff==false)
			 		{
			 			 actions.moveToElement(driver.findElement(Locators.cancelLease)).build().perform();
			 			 driver.findElement(Locators.cancelLease).click();
			 			 return true;
			 		}
			 	    else 
			 	    {
			 	    	 actions.moveToElement(driver.findElement(Locators.saveLease)).build().perform();
			 			 driver.findElement(Locators.saveLease).click();
			 			 PropertyWare.evictionPopUp(driver);
			 			 Thread.sleep(2000);
			 			 try
			 			 {
			 				 driver.switchTo().alert().accept();
			 				 failedReason = failedReason + "";
			 				RunnerClass.setFailedReason(failedReason);
			 			 }
			 			 catch(Exception e) {}
			 			 try
			 			 {
			 			 if(driver.findElement(Locators.saveLease).isDisplayed())
			 			 {
			 				 failedReason = failedReason + "Base Rent could not be saved";
			 				RunnerClass.setFailedReason(failedReason);
			 				 return false;
			 			 }
			 			 }
			 			 catch(Exception e) {}
			 			 return true;
			}
 			
			}	
		}
		catch(Exception e)
		{
			failedReason = failedReason + "Base Rent could not be saved";
			RunnerClass.setFailedReason(failedReason);
			return false;
		}
	}

}
