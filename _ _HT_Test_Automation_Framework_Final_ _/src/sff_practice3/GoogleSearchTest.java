package sff_practice3;

import static sff_practice3.DriverType.CHROME;
import static sff_practice3.DriverType.FIREFOX;

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
/***
 * 
 * @code DriverFactory
 *
 */
class DriverFactory {
	private static ThreadLocal<WebDriver> tl =
			new ThreadLocal<WebDriver>();

	@BeforeMethod
	//IMPORTANT! DO NOT MAKE createWebDriver STATIC!!! WILL NOT WORK!
	public WebDriver createWebDriver() throws Exception{ 
		DriverType dt = DriverType.CHROME;
		WebDriver driver = null;
		
		switch(dt){
		case CHROME:
			driver = CHROME.getWebDriver();
			break;
		case FIREFOX:
			driver = FIREFOX.getWebDriver();
			break;
		default:
			throw new Exception("DriverFactory."
					+ "createWebDriver: Undefined driver type.");
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
/**
 * 
 * @code GoogleSearchPage
 *
 */

class GoogleSearchPage{	
	private String URL = "http://google.com";
	private static final long WAIT_TIMEOUT = 30;

	public GoogleSearchPage(){
		PageFactory.initElements(DriverFactory.getWebDriver(),this);
		DriverFactory.getWebDriver().get(URL);
	}

	@FindBy(name = "q")
	WebElement searchTxtBox;

	public void searchFor(String term) throws Exception{
		WebDriver d = DriverFactory.getWebDriver();

		//wait for searchTxtBox to appear
		new WebDriverWait(d, WAIT_TIMEOUT)
		.until(ExpectedConditions.visibilityOf(searchTxtBox));

		//send search term and submit
		searchTxtBox.sendKeys(term);
		searchTxtBox.submit();
	}

}

class SeleniumBase extends DriverFactory {
	protected static final long WAIT_TIMEOUT = 30; //secs
}

public class GoogleSearchTest extends SeleniumBase {

	private void googleSearchTest(String term){	
		try{
			//arrange
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act
			gsp.searchFor(term);

			//assert
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
					.until(ExpectedConditions.titleContains(term))
					);
		}
		catch(Exception e){
			System.out.println("GoogleSearchTest Exception:" + e.getMessage());
		}
	}

	@Test(dataProvider = "testData")
	public void test(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testData(){
		return new Object[][]{
				{"seattle sounders"},
				{"green bay"},
				{"chicago bears"}};
	}
}
