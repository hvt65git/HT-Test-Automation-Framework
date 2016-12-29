package sff_practice6;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static sff_practice6.DriverType.CHROME;
import static sff_practice6.DriverType.FIREFOX;

interface Driver {
	public WebDriver getWebDriver();
}

enum DriverType implements Driver {
	CHROME{
		public WebDriver getWebDriver() {
			
			//bugbug: only mistake was here: "chrome.web.driver"
			//must have been "webdriver.chrome.driver", (WCD mnemonic)
//			System.setProperty("chrome.web.driver",
//					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
//			
			
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			
			return new ChromeDriver();
		}
	},
	FIREFOX{
		public WebDriver getWebDriver() {
			return new FirefoxDriver();
		}
	}
}

class DriverFactory {
	private static DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl =
			new ThreadLocal<WebDriver>();

	@BeforeMethod
	public WebDriver createWebDriver() throws IllegalAccessException{
		WebDriver driver = null;
		switch(this.dt){
		case CHROME:
			driver = CHROME.getWebDriver();
			break;
		case FIREFOX:
			driver = FIREFOX.getWebDriver();
		default: 
			throw new 
			IllegalAccessException("DriverFactory.createWebDriver"
					+ " Unknown driver type.");
		}
		setWebDriver(driver);
		return getWebDriver();
	}

	private void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}

	public static WebDriver getWebDriver() {
		return tl.get();
	}

	@AfterMethod()
	public void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory {
	protected static final long WAIT_TIMEOUT = 30; //sec

	//set props for log4j etc

	//read in test data from excel sheet using poi
	protected Object[][] testData =
		{	{"seattle"},
			{"sounders"},
			{"seahawks"}, 
			{"storm"},
			{"sonics"},
			{"mariners"}
		};

}

class GoogleLoginPage {
	private static final long WAIT_TIMEOUT = 30;//sec
	private static final String URL = "http://google.com";

	@FindBy(name = "q")
	WebElement searchBox;

	public GoogleLoginPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	public GoogleLoginPage search(String term) throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for search box to appear - by proxy
		new WebDriverWait(driver, WAIT_TIMEOUT )
			.until(ExpectedConditions.visibilityOf(searchBox));

		//set the search text and submit the search
		searchBox.sendKeys(term);
		searchBox.submit();
		return this;
	}
}

public class GoogleSearchTest extends SeleniumBase {

	public void googleSearchTest(String term) {

		try {
			//arrange - create test object
			GoogleLoginPage glp = new GoogleLoginPage();

			//act - perform search
			glp.search(term);

			//assert - verify the search result
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT).
					until(ExpectedConditions.titleContains(term))
					);
		}
		catch(Exception e){
			System.out.println("GoogleSearchTest.googleSearchTest"
					+ " Exception: " + e.getMessage());
		}
	}


	@Test(dataProvider = "getTestData")
	public void test(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][]getTestData(){
		return testData;
	}
}
