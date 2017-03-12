package sff_25;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static sff_25.DriverType.CHROME;
import static sff_25.DriverType.FIREFOX;


interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME {
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX {
		public WebDriver getDriver() {
			return new FirefoxDriver();
		}
	}
}

class DriverFactory {
	private final DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>();
	
	public static WebDriver getWebDriver() {
		return tl.get();
	}
	
	private void setWebDriver(WebDriver driver) {
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
	private Object[][] testData;
	
	protected void setTestData(Object[][] testData) {
		this.testData = testData;
	}
	
	protected Object[][] getTestData() {
		return testData;
	}
}

class GoogleSearchPage {
	
}

public class GoogleSearchSmokeTest1 extends SeleniumBase {
	
	@Test(dataProvider = "dataProvider")
	public void test01(String term) {
		System.out.println("current term is: " + term);
	}
	
	@DataProvider
	public Object[][] dataProvider() {
		setTestData(new Object[][]{
				{"seattle seahawks"},
				{"seattle storm"},
				{"seattle mariners"}});
		return getTestData();
	}
}












