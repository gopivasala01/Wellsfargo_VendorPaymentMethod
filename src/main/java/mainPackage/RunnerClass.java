package mainPackage;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;


public class RunnerClass {
	
    public static String[][] pendingLeases;
	public static String downloadFilePath;
	public static WebDriverWait wait;
	
	public static String previousRecordCompany;
	public static boolean loggedOut = false;
    

    // Use ThreadLocal to store a separate ChromeDriver instance for each thread
    private static ThreadLocal<ChromeDriver> driverThreadLocal = new ThreadLocal<ChromeDriver>();
    private static ThreadLocal<String> baseRentAmountThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> portfolioNameThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> baseRentFromPWThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> failedReasonThreadLocal = new ThreadLocal<>();

    @BeforeMethod
    public boolean setUp() throws InterruptedException {
        // Set up WebDriverManager to automatically download and set up ChromeDriver
    	//System.setProperty("webdriver.http.factory", "jdk-http-client");
        WebDriverManager.chromedriver().setup();
        RunnerClass.downloadFilePath = AppConfig.downloadFilePath;
		Map<String, Object> prefs = new HashMap<String, Object>();
	    // Use File.separator as it will work on any OS
	    prefs.put("download.default_directory",RunnerClass.downloadFilePath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        WebDriverManager.chromedriver().clearDriverCache().setup();
        // Create a new ChromeDriver instance for each thread
        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Store the ChromeDriver instance in ThreadLocal
        driverThreadLocal.set(driver);
        driver.get(AppConfig.URL);
        driver.findElement(Locators.userName).sendKeys(AppConfig.username);
        driver.findElement(Locators.password).sendKeys(AppConfig.password);
        Thread.sleep(2000);
        driver.findElement(Locators.signMeIn).click();
        Thread.sleep(3000);
        wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try
        {
        if(driver.findElement(Locators.loginError).isDisplayed())
        {
        	System.out.println("Login failed");
			return false;
        }
        }
        catch(Exception e) {}
		return true;
        
        
    }

    @Test(dataProvider = "testData")
    public void testMethod(String ID,String company, String leaseEntityID,String dateDifference,String moveInDate) throws Exception {
    	String portfolioName="";
    	String baseRentAmount ="";
    	String baseRentFromPW="";
    	String failedReason="";
    	
    	System.out.println("<-------- "+leaseEntityID+" -------->");
    	// Retrieve the thread-specific ChromeDriver instance from ThreadLocal
        ChromeDriver driver = driverThreadLocal.get();
        if(company.equalsIgnoreCase("Chicago PFW")) {
			   company = "Chicago";
		  }
		if(company.equalsIgnoreCase("California PFW")) {
			   company = "California pfw";
		  }
		try {
			String expiredURL = driver.getCurrentUrl();
			if(expiredURL.contains("https://app.propertyware.com/pw/expired.jsp") || expiredURL.equalsIgnoreCase("https://app.propertyware.com/pw/expired.jsp?cookie") || expiredURL.contains(AppConfig.URL)) {
				loggedOut = true;
				driver.navigate().to(AppConfig.URL);
				driver.findElement(Locators.userName).sendKeys(AppConfig.username); 
				driver.findElement(Locators.password).sendKeys(AppConfig.password);
			    Thread.sleep(2000);
			    driver.findElement(Locators.signMeIn).click();
			    Thread.sleep(3000);
			}
		}
		catch(Exception e) {}
		if (PropertyWare.selectLease(driver,company,leaseEntityID) == false) {
			portfolioName = getPortfolioName();
			failedReason = getFailedReason();
			String query = "Update Automation.BaseRentUpdate set Automation_Status='Failed',Automation_Notes='"+ failedReason + "',Automation_CompletionDate =getdate() where ID = '" + ID + "'";
			DataBase.updateTable(query);
			previousRecordCompany = company;	
			
		}
		else {
			portfolioName = getPortfolioName();
			//loggedOut = false;
			//previousRecordCompany = company;
			if (UpdateBaseRent.getBaseRentAmount(driver,company,dateDifference,moveInDate) == false) {
				baseRentAmount = getBaseRentAmount();
				baseRentFromPW = getBaseRentFromPW();
				failedReason = getFailedReason();
				String query = "Update Automation.BaseRentUpdate set Automation_Status='Failed',Automation_Notes='"
						+ failedReason + "',Automation_CompletionDate =getdate(),BaseRentFromAutoCharges='"
						+ baseRentAmount + "',BaseRentFromPW = '" + baseRentFromPW + "',PortfolioName ='"+ portfolioName +"' where ID = '" + ID + "'";
				DataBase.updateTable(query);
				
			}
			else {
				baseRentAmount = getBaseRentAmount();
				baseRentFromPW = getBaseRentFromPW();
				if (UpdateBaseRent.updateBaseRent(driver) == false) {
					failedReason = getFailedReason();
					String query = "Update Automation.BaseRentUpdate set Automation_Status='Failed',Automation_Notes='"
							+ failedReason + "',Automation_CompletionDate =getdate(),BaseRentFromAutoCharges='"
							+ baseRentAmount + "',BaseRentFromPW = '" + baseRentFromPW + "',PortfolioName ='"+ portfolioName +"' where ID = '" + ID + "'";
					DataBase.updateTable(query);
					
				}
				else {
					// Update table for successful lease
					try {
						System.out.println("Base Rent Updated");
						failedReason = getFailedReason();
						String query = "Update Automation.BaseRentUpdate set Automation_Status='Completed',Automation_Notes='"+ failedReason + "',Automation_CompletionDate =getdate(),BaseRentFromAutoCharges='"
								+ baseRentAmount + "',BaseRentFromPW = '" + baseRentFromPW + "',PortfolioName ='"+ portfolioName +"' where ID = '" + ID + "'";
						DataBase.updateTable(query);
						
					} catch (Exception e) {}
				}
			}
		}
		
        
    }

    public static String getBaseRentAmount() {
        return baseRentAmountThreadLocal.get();
    }

    public static void setBaseRentAmount(String baseRentAmount) {
        baseRentAmountThreadLocal.set(baseRentAmount);
    }
    public static String getPortfolioName() {
        return portfolioNameThreadLocal.get();
    }

    public static void setPortfolioName(String portfolioName) {
    	portfolioNameThreadLocal.set(portfolioName);
    }
    public static String getBaseRentFromPW() {
        return baseRentFromPWThreadLocal.get();
    }

    public static void setBaseRentFromPW(String baseRentFromPW) {
    	baseRentFromPWThreadLocal.set(baseRentFromPW);
    }
    public static String getFailedReason() {
        return failedReasonThreadLocal.get();
    }

    public static void setFailedReason(String failedReason) {
    	failedReasonThreadLocal.set(failedReason);
    }
    
        
        
        // Add your test code here

   @AfterMethod
    public void tearDown() {
        // Quit the thread-specific ChromeDriver instance
        ChromeDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
        }
    }

    @DataProvider(name = "testData", parallel = true)
    public Object[][] testData() {
    	DataBase.getLeasesList(AppConfig.pendingLeasesQuery);
        return pendingLeases;
    }
}