package framework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * 
 * @author focalpt
 *
 */

public enum BrowserType implements Browser{
	CHROME{
		@Override
		public WebDriver getWebDriver(){
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX{
		@Override
		public WebDriver getWebDriver(){
			return new FirefoxDriver();
		}
	}
}