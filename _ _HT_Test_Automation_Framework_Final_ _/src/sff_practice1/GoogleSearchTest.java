package sff_practice1;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
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

import com.google.common.base.Predicate;

interface Driver {
	WebDriver getWebDriver();
}
enum DriverType implements Driver{
	FIREFOX{
		public WebDriver getWebDriver(){
			return new FirefoxDriver();
		}

	},
	CHROME{
		public WebDriver getWebDriver(){
			System.setProperty("webdriver.chrome.driver", 
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	}
}

class DriverFactory{
	private static final DriverType dt = DriverType.CHROME;
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>();

	@BeforeMethod
	protected static WebDriver createWebDriver()
			throws IllegalAccessException {
		WebDriver driver = null;

		switch(dt){
		case CHROME:
			driver = DriverType.CHROME.getWebDriver();
			break;
		case FIREFOX:
			driver = DriverType.FIREFOX.getWebDriver();
			break;
		default:
			throw new IllegalAccessException();
		}
		setWebDriver(driver);
		return getWebDriver();
	}

	private static void setWebDriver(WebDriver driver){
		tl.set(driver);
	}

	protected static WebDriver getWebDriver(){
		return tl.get();
	}

	@AfterMethod
	protected static void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory{
	protected Wait<WebDriver> wait;
	protected final long WAIT_TIMEOUT; //seconds
	
	protected SeleniumBase(long timeout){
		WAIT_TIMEOUT = timeout;
		System.setProperty("log4j.configuration", "set") ;
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.Jdk14Logger");
	}
}


class GoogleSearchPage{
	
	private final long WAIT_TIMEOUT; //if static final cannot be assigned in constructor
	private final String URL = "http://google.com";

	@FindBy(name = "q")
	WebElement txtSearchBox;

	public GoogleSearchPage(long timeout){
		WAIT_TIMEOUT = timeout;
		PageFactory.initElements(DriverFactory.getWebDriver(), this);
	}

	public void google_search(String term) throws Exception{
		//launch URL
		DriverFactory.getWebDriver().get(URL);

		//wait for txt box to appear
		new WebDriverWait(DriverFactory.getWebDriver(),WAIT_TIMEOUT)
		.until(ExpectedConditions.visibilityOf(txtSearchBox));

		//submit search term
		txtSearchBox.sendKeys(term);
		txtSearchBox.submit();
	}

}

public class GoogleSearchTest extends SeleniumBase{
	
	public GoogleSearchTest(){
		super(10); //calling super constructor and passing timeout value
		
	}
	
	public void GoogleTest(String term){
		try{
			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage(WAIT_TIMEOUT);

			//act - search for term
			gsp.google_search(term);

			//verify - wait for page title to contain the search term
			wait = new WebDriverWait(getWebDriver(), WAIT_TIMEOUT);
			Assert.assertTrue(wait.until(d->d.getTitle().contains(term)));
		}
		catch(Exception e){
			System.out.println("***GoogleTest - Exception: "
					+  e.getMessage());
		}
	}

	@Test(dataProvider = "getTestData")
	public void test(String term){
		GoogleTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] getTestData(){
		return new Object[][]{
				{"sounders"},
				{"seahawks"},
				{"sonics"},
				{"storm"},
				{"sounders MLS champions"},
				{"seahawks NFL champions"},
				{"storm WNBA champions"},
				{"sonics NBA champions"}
		};
	}
}




