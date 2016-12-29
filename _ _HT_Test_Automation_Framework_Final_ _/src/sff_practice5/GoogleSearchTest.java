package sff_practice5;



import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static sff_practice5.DriverType.CHROME;
import static sff_practice5.DriverType.FIREFOX;

interface Driver{
	public WebDriver getWebDriver();
}

enum DriverType implements Driver{
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

class DriverFactory {
	private static ThreadLocal<WebDriver> tl =
			new ThreadLocal<WebDriver>();

	@BeforeMethod
	public WebDriver createWebDriver() throws IllegalAccessException{
		WebDriver driver = null;
		DriverType dt = CHROME;

		switch(dt){
		case CHROME:
			driver = CHROME.getWebDriver();
			break;
		case FIREFOX:
			driver = FIREFOX.getWebDriver();
			break;
		default:
			throw new IllegalAccessException();
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
		tl.get().quit();
		tl.remove();
	}

}

class SeleniumBase extends DriverFactory{
	protected static final long WAIT_TIMEOUT = 30; //secs
}

class GoogleSearchPage{
	private static final String URL = "http://google.com";
	private static final long WAIT_TIMEOUT = 30; //secs
	private static final Class<? extends Throwable> StaleReferenceException = null;

	@FindBy(name = "q")
	private WebElement searchBox;

	public GoogleSearchPage() throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
	}

	public void search_google(String term){
		WebDriver driver = DriverFactory.getWebDriver();

		new WebDriverWait(driver, WAIT_TIMEOUT)
			.until(ExpectedConditions.visibilityOf(searchBox));

		//submit search
		searchBox.sendKeys(term);
		searchBox.submit();
	}
}

public class GoogleSearchTest extends SeleniumBase {

	public void googleSearchTest(String term){
		try{
			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - perform search
			gsp.search_google(term);

			//assert - verify search result contains term in page title
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
					.until(ExpectedConditions.titleContains(term))
					);
		}
		catch(Exception e){
			System.out.println("GoogleSearchTest exception:" + e.getMessage());
		}
	}

	@Test(dataProvider = "getTestData")
	public void test(String term){
		googleSearchTest(term);
	}
	
	@DataProvider(parallel = true)
	public Object[][] getTestData(){
		return new Object[][]{
				{"mt. shasta"},
				{"panda bear"},
				{"tokyo tower"}
		};
	}
}





















