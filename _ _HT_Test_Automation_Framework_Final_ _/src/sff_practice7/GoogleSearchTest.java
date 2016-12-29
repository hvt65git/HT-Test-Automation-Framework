package sff_practice7;

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

import static sff_practice7.DriverType.CHROME;
import static sff_practice7.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME{
		public WebDriver getDriver(){
			System.setProperty("webdriver.chrome.driver", 
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX{
		public WebDriver getDriver(){
			return new FirefoxDriver();
		}
	}
}

class DriverFactory {
	private static ThreadLocal<WebDriver> tl = 
			new ThreadLocal<WebDriver>();

	@BeforeMethod
	public WebDriver createWebDriver() throws Exception{
		DriverType dt = CHROME;
		WebDriver driver = null;

		switch(dt){
		case CHROME:
			driver = CHROME.getDriver();
			break;
		case FIREFOX:
			driver = FIREFOX.getDriver();
			break;
		default: 
			throw new Exception();
		}

		setWebDriver(driver);
		return getWebDriver();
	}

	public static WebDriver getWebDriver(){
		return tl.get();
	}

	private void setWebDriver(WebDriver driver){
		tl.set(driver);
	}

	@AfterMethod
	public void releaseWebdriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory {

	protected static long WAIT_TIMEOUT = 30; //sec
	//init log4j here

	protected Object[][] testData =
		{
			{"seattle seahawks"},
			{"Paul Allen"},
			{"seattle seahawks"},
			{"Paul Allen"},
			{"seattle seahawks"},
			{"Paul Allen"},
			{"seattle seahawks"},
			{"Paul Allen"},
			{"seattle seahawks"},
			{"Paul Allen"}
		};
}

class GoogleSearchPage {
	private static final long WAIT_TIMEOUT = 30; //sec
	private static final String URL = "http://google.com";

	@FindBy(name = "q")
	WebElement searchBox;

	public GoogleSearchPage(){
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver,this);
		driver.get(URL);
		driver.manage().window().maximize();	                                                                                                                                              
	}

	public void google_search(String term) throws Exception{
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for text box to appear - by proxy
		new WebDriverWait(driver, WAIT_TIMEOUT)
		.until(ExpectedConditions.visibilityOf(searchBox));

		//set the text then submit it
		searchBox.sendKeys(term);
		searchBox.submit();
	}
}


public class GoogleSearchTest extends SeleniumBase {

	private void googleSearchTest(String term){
		try{
			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - search
			gsp.google_search(term);

			//assert - verify search result
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.until(ExpectedConditions.titleContains(term))
					);
		}
		catch(Exception e){
			System.out.println("GoogleSearchTest Exception: "
					+ e.getMessage());
		}
	}

	@Test(dataProvider = "getTestData" )
	public void test(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] getTestData() {
		return testData;
	}

}
