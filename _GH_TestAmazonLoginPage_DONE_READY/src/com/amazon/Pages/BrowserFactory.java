package com.amazon.Pages;

import java.io.File;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class BrowserFactory {
	 static WebDriver driver;
	 static long implicitTimeOutSeconds;
	
	
	private static void launchBrowser(String URL){
		driver.get(URL);
		driver.manage().window().maximize();
		//driver.manage().timeouts().implicitlyWait(implicitTimeOutSeconds,TimeUnit.SECONDS);	
	}
	
	public static WebDriver startBrowser(BrowserType type, String URL, long implicitTimeOutSeconds) throws Exception{	
		switch(type){
		case FIREFOX:
			try{
				FirefoxBinary binary = new FirefoxBinary(new File("C:\\Program Files (x86)\\Mozilla Firefox\\Older Build\\FF47.0.1\\firefox.exe"));
				File defaultProfileDirOldVersion     = new File("C:\\Users\\Older Firefox\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\wsnh2xq3.default");
				File defaultProfileDirCurrentVersion = new File("C:\\Users\\focalpt\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\y2ni8rp0.default");	
				FirefoxProfile profile = new FirefoxProfile(defaultProfileDirOldVersion);
				driver = new FirefoxDriver(binary, profile);
				launchBrowser(URL);
			}
			catch(Exception e){
				System.out.println("***ht debug: " + e.getMessage());
			}
			break;
		case CHROME:
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			driver = new ChromeDriver();
			launchBrowser(URL); 
			
			//cancel the alert popup
			String currentHandle = driver.getWindowHandle();
			System.out.println("Current window handle = " + currentHandle);
			
			Set<String> handles = driver.getWindowHandles();
			for(String current : handles){
				System.out.println("iterated window handle = " + current);
				if(!currentHandle.equals(current)){
					driver.switchTo().window(current);
					System.out.println("current window title = " + driver.getTitle());
				}
			}
			break;
		case IE:
			driver = new InternetExplorerDriver();
			launchBrowser(URL);
			break;			
		default:
			throw new Exception("Undefined browser type.");
		}


		
		return driver;
	}

}