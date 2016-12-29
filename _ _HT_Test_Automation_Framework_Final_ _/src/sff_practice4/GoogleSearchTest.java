package sff_practice4;

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

import static sff_practice4.DriverType.CHROME;
import static sff_practice4.DriverType.FIREFOX;

interface Driver{
	WebDriver getWebDriver();
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
	private static ThreadLocal<WebDriver> tl  =
			new ThreadLocal<WebDriver>(){//anon innerclass demo
				protected WebDriver initialValue() {
					return null;
		}
	};

	@BeforeMethod
	//IMPORTANT! DO NOT MAKE createWebDriver STATIC!!! WILL NOT WORK!
	public WebDriver createWebDriver() throws IllegalAccessException {
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
	//IMPORTANT! DO NOT MAKE releaseWebDriver STATIC!!! WILL NOT WORK!
	public void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory{
	protected static final long WAIT = 15; //secs
}

class GoogleSearchPage{

	@FindBy(name = "q")
	private WebElement searchBox;
	private final String URL = "http://google.com";

	public GoogleSearchPage(){
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	public void search_google(String term) throws Exception {
		
		//wait for search box to display by proxy
		new WebDriverWait( DriverFactory.getWebDriver(),30)
			.until(ExpectedConditions.visibilityOf(searchBox));

		//submit the search
		searchBox.sendKeys(term);
		searchBox.submit();
	}

}

public class GoogleSearchTest extends SeleniumBase {

	public void googleSearchTest(String term){
		try{
			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage();
			
			//act - invoke method(s)
			gsp.search_google(term);
			
			//assert - wait and verify title on resultant page contains search term
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),30)
						.until(ExpectedConditions.titleContains(term)));
		}
		catch(Exception e){
			System.out.println("GoogleSearchTest.googleSearchTest"
							+ " Exception encountered: " + e.getMessage());
		}
	}

	@Test(dataProvider = "testData")
	public void test(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testData(){
		return new Object[][]{
				{"skylab"}, 
				{"JPL"},
				{"NASA"}, 
				{"lyra constellation"},
				{"sitara"}
				};
	}
}










