package singlefileframework;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/*********************************************************************
 * 
 * @author htodd    -My Selenium and TestNG framework in a single file
 * 					 supports multithreaded testing when multiple test
 * 					 annotations are used and when the dataprovider 
 * 					 is used for a single test annotation when 
 * 					 parallel = true is set in @DataProvider argument
 * 
 *********************************************************************
 */

interface Driver{
	public WebDriver getWebDriver();
}

enum DriverType implements Driver{
	CHROME{
		@Override
		public WebDriver getWebDriver(){
			DesiredCapabilities dc;
			System.setProperty("webdriver.chrome.driver", 
					System.getProperty("user.dir") + 
					"\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX{
		@Override
		public WebDriver getWebDriver(){
			DesiredCapabilities dc;
			return new FirefoxDriver();
		}
	}
}

class DriverFactory{
	//use ThreadLocal to associate a WebDriver object with a thread 
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>();

	protected DriverFactory(){
		System.out.println("DriverFactory constructor called...");
	}

	protected static WebDriver createWebDriver(DriverType bt) 
			throws Exception{
		WebDriver driver = null;
		switch(bt){
		case CHROME:
			driver = DriverType.CHROME.getWebDriver();
			break;
		case FIREFOX:
			driver = DriverType.FIREFOX.getWebDriver();
			break;
		default:
			throw 
			new IllegalAccessException("createWebDriver() -"
					+ " IllegalAccessException");
		};
		setWebDriver(driver);
		getWebDriver().manage().window().maximize();	
		return getWebDriver();
	}

	private static void setWebDriver(WebDriver driver){
		tl.set(driver);
	}

	protected static WebDriver getWebDriver(){
		return tl.get();
	}

	protected static void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory{
	
	protected SeleniumBase(){
		System.out.println("DEBUG: SeleniumBase constructor called...");
		System.setProperty("log4j.configuration", "set") ;
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.Jdk14Logger");
	}
}

/***
 * 
 * @code GoogleSearchPage - 
 * Uses the PageFactory Page object model for 
 * representing the main google page 
 *
 */
class GoogleSearchPage{
	private WebDriver driver;
	private Wait<WebDriver> wait;
	private static final String URL = "https://www.google.com";

	@FindBy(name = "q")
	private WebElement searchme;

	//use @FindBys to guess at which elements are the correct one
	//	@FindBys({	
	//		@FindBy(id = "gs_htif0"),   
	//		@FindBy(name="q"), 			//preceding sibling
	//		@FindBy(id = "gs_lc0"),		//.//*[@id='gs_htif0']/..
	//		@FindBy(id = "sb_ifc0")})   //.//*[@id='gs_htif0']/../..	
	//	@CacheLookup

	public GoogleSearchPage(WebDriver d){
		this.driver = d;

		//init proxy class using static PageFactory obj
		PageFactory.initElements(driver,this);

		//init wait object
		wait = new WebDriverWait(driver, 10)
		.ignoring(StaleElementReferenceException.class);

		//launch website and maximize the  browser page
		driver.get(URL);
		driver.manage().window().maximize();
	}

	public void search_google(String txt)
			throws TimeoutException, Exception{
		wait.until(ExpectedConditions.visibilityOf(searchme)); 
		searchme.sendKeys(txt);
		searchme.submit();
	}

}
/*********************************************************************
 * @ class GoogleTest - The test class which contains the @Test and 
 *               @BeforeMethod for initialing a thread safe web driver
 *               object for each test.
 *               Also contains @AfterMethod for closing each browser
 *               properly and releasing each web driver object
 *********************************************************************
 */
public class GoogleTest extends SeleniumBase {
	private static final long timeout = 10; //secs

	public GoogleTest(){
		System.out.println("Debug: GoogleSearchTest "
				+ "constructor called....");
	}

	public void executeGoogleSearchTest(final String term){
		try{
			//arrange
			GoogleSearchPage page
			= new GoogleSearchPage(getWebDriver());

			//act
			page.search_google(term);

			//assert
			Assert.assertTrue(new WebDriverWait(getWebDriver(),timeout)
			.until(ExpectedConditions.titleContains(term)));
		}
		catch(Exception e){
			System.out.println("The following error occurred: " 
					+ e.getMessage());
		}
	}

	@BeforeMethod 
	public void init(){
		try{
			DriverFactory.createWebDriver(DriverType.CHROME);
		}
		catch(Exception e){
			System.out.println("The following error "
					+ "occurred during init(): " + e.getMessage());
		}
	}

	@AfterMethod
	public void cleanup(){
		DriverFactory.releaseWebDriver();
	}

	@Test(dataProvider="testData")
	public void test1(String term){
		executeGoogleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testData(){
		return new Object[][]{
				{"frogs"},{"lion"},{"seattle"},{"seahawks"}, 
				{"sounders"},{"magic mountain"},{"disneyland"},
				{"marshawn lynch"},{"beast mode"},{"pete carroll"}
		};
	}
}
