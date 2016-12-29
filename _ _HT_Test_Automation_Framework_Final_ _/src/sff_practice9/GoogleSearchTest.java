package sff_practice9;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static sff_practice9.DriverType.CHROME;
import static sff_practice9.DriverType.FIREFOX;

interface Driver{
	public WebDriver getWebDriver();
}

enum DriverType implements Driver {
	CHROME{
		public WebDriver getWebDriver()  {
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
	private static final DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl =
			new ThreadLocal<WebDriver>();

	@BeforeMethod
	public WebDriver createWebDriver() throws Exception {
		WebDriver driver = null;

		switch(dt){
		case FIREFOX:
			driver = FIREFOX.getWebDriver();
			break;
		case CHROME:
			driver = CHROME.getWebDriver();
			break;
		default:
			throw new 
			Exception("Illegal DriverType specified.");
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

class SeleniumBase extends DriverFactory {
	protected static final long WAIT_TIMEOUT = 30; //seconds
	protected Object[][] getTestData() {
		return new Object[][]{
				{"seattle sounders"},
				{"seahawks"},
				{"sonics"},
				{"seattle storm"},
				{"seattle reign"}
		};
	}
}

class GoogleSearchPage {
	private static final String URL = "http://google.com";
	protected static final long WAIT_TIMEOUT = 30; //seconds

	public GoogleSearchPage(){
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver,this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@FindBy(name = "q")
	private WebElement txtBox;

	public void searchGoogle(final String term) {
		WebDriver driver = DriverFactory.getWebDriver();

		try{

			//wait (by proxy) for text box to appear
			new WebDriverWait(driver,WAIT_TIMEOUT)
			.until(ExpectedConditions.visibilityOf(txtBox));
		}
		catch(Exception e){
			e.getMessage();
		}
		//send the text and submit the search
		txtBox.sendKeys(term);
		txtBox.submit();
	}
}

public class GoogleSearchTest extends SeleniumBase {

	public void googleSearchTest(String term) {

		try{
			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - search
			gsp.searchGoogle(term);

			//assert result
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), 30)
					.until(ExpectedConditions.titleContains(term))
					);
		}
		catch(Exception e){
			System.out.println("googleSearchTest: " +
					e.getMessage());
		}
	}
	@Test(dataProvider = "testDataProvider")
	public void test(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testDataProvider(){	
		return getTestData();	
	}
}


/* RESULT:
 * got this code correct on first try!
 */
