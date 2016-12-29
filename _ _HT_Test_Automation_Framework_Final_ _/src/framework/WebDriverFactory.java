package framework;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;

/***
 * 
 * WebDriverFactory - instantiates desired browser object and returns the raw driver
 *
 */
public class WebDriverFactory{

//	
//	public  WebDriver create(BrowserType bt) throws IllegalAccessException{
//		WebDriver driver = null;
		
		
	public  static WebDriver create(BrowserType bt) throws IllegalAccessException{
		WebDriver driver = null;
		
		switch(bt){
		case CHROME:
			driver = BrowserType.CHROME.getWebDriver();
			break;
		case FIREFOX:
			driver = BrowserType.FIREFOX.getWebDriver();
			break;
		default:
			throw new IllegalAccessException("In WebDriver create. Error - Undefined BrowserType");
		}
		
		return driver;
	}
}
