package sff_practice_21;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import static sff_practice_21.DriverType.CHROME;
import static sff_practice_21.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME {
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver(){};
		}
	},
	FIREFOX {
		public WebDriver getDriver() {
			return new FirefoxDriver(){};
		}
	};	
}

class DriverFactory {
	private DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>() {
		public WebDriver initialize() {
			return null;
		}
		public void remove() {
			super.remove();
			System.out.println("test test test - just invoked super.remove");
		}
		public WebDriver get() {
			System.out.println("test test test - just invoked super.get.");
			return super.get();
		}
	};
	
	public static WebDriver getWebDriver() {
		return tl.get();
	}
	
	public void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}
	
	@BeforeMethod
	public WebDriver createWebDriver() {
		setWebDriver(dt.getDriver());
		return getWebDriver();
	}
	
	@AfterMethod
	public void releaseWebDriver() {
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory {
	protected final long WAIT_TIMEOUT = 20; //sec
	private final Object[][] testData = { 	
			{"green prehnite"},
			{"seattle seahawks"},
			{"toronto blue jays"}
		};
	public Object[][] getTestData() {
		return testData;
	}	
}

class GoogleSearchPage {
	private final String URL = "http://google.com";
	private final long WAIT_TIMEOUT = 20;
	
	@FindBy(xpath = "//*[@name='q']")
	WebElement txtBox;
	
	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver,this);
		driver.get(URL);
		driver.manage().window().maximize();
	}
	
}

public class GoogleSmokeTest extends SeleniumBase {

}
