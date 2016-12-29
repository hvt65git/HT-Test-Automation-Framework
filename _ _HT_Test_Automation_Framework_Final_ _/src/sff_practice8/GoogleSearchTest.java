package sff_practice8;

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

import static sff_practice8.DriverType.CHROME;
import static sff_practice8.DriverType.FIREFOX;

interface Driver {
	public WebDriver getWebDriver();
}

enum DriverType implements Driver {
	CHROME{
		public WebDriver getWebDriver(){
			System.setProperty("webdriver.chrome.driver", 
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX{
		public WebDriver getWebDriver(){
			return new FirefoxDriver();
		}	
	}
}


class DriverFactory{
	private static ThreadLocal<WebDriver> tl =
			new ThreadLocal<WebDriver>();

	@BeforeMethod
	public WebDriver createWebDriver() throws Exception {
		DriverType dt = CHROME;
		WebDriver driver = null;

		switch(dt){
		case CHROME:
			driver = CHROME.getWebDriver(); 
			break;
		case FIREFOX:
			driver = FIREFOX.getWebDriver(); 
			break;
		default:
			throw new Exception("unspecified DriverType");
		}
		setWebDriver(driver);
		return getWebDriver();
	}

	private void setWebDriver(WebDriver driver){
		tl.set(driver);
	}

	public static WebDriver getWebDriver(){
		return tl.get();
	}

	@AfterMethod
	public void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}


class SeleniumBase extends DriverFactory{
	protected static final long WAIT_TIMEOUT = 30; //sec
	protected Object[][] testData = {
			{"seahawks"},
			{"sonics"},
			{"seattle storm"},
			{"mariners"},
			{"sounders"}
	};
}


class GoogleSearchPage {
	private static final String URL = "http://google.com";
	private static final long WAIT_TIMEOUT = 30; //sec

	public GoogleSearchPage(){
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@FindBy(name = "q")
	private WebElement searchBox;

	public void googleSearch(String term){
		WebDriver driver = DriverFactory.getWebDriver();

		//wait until searchBox appears
		new WebDriverWait(driver, WAIT_TIMEOUT)
		.until(ExpectedConditions.visibilityOf(searchBox));

		//then enter search text and submit the search
		searchBox.sendKeys(term);
		searchBox.submit();
	}
}

public class GoogleSearchTest extends SeleniumBase{

	private void googleSearchTest(String term){
		try{
			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - invoke search
			gsp.googleSearch(term);

			//assert - verify page title contains search term
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
					.until(ExpectedConditions.titleContains(term))
					);
		}
		catch(Exception e){
			System.out.println("GoogleSearchTest - Exception: " +
					e.getMessage());

		}
	}

	@Test(dataProvider = "getTestData")
	public void test(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] getTestData(){
		return testData;
	}
}
/* RESULT:
 * got this code correct on first try, finally!
 */
