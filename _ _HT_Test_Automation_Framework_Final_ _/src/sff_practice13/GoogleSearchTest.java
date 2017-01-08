package sff_practice13;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
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

import com.google.common.base.Predicate; //apply
//import java.util.function.Predicate;


import static sff_practice13.DriverType.CHROME;
import static sff_practice13.DriverType.FIREFOX;

interface Driver {
	WebDriver getDriver();
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
	private static final DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>();

	@BeforeMethod
	public WebDriver createWebDriver() throws IllegalAccessException{
		WebDriver driver = null;
		switch(dt) {
		case CHROME:
			driver = CHROME.getDriver();
			break;
		case FIREFOX:
			driver = FIREFOX.getDriver();
			break;
		default:
			throw new IllegalAccessException(
					"DriverFactory.createWebDriver:"
							+ " Error - Undefined driver type.");
		}
		setWebDriver(driver);
		return getWebDriver();
	}

	public static WebDriver getWebDriver() {
		return tl.get();
	}

	public void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}

	@AfterMethod
	public void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

abstract class SeleniumBase extends DriverFactory {
	//set a global timeout
	protected static final long WAIT_TIMEOUT = 20; //sec

	// return some test data
	protected Object[][] getData(){
		Object[][] testData = {
				{"seattle"},{"seahawks"},{"Super Bowl"}
		};
		return testData;
	}

	//TODO: Load and set properties for log4j
	//TODO: Load properties from all .properties file
}

class GoogleSearchPage {
	private static final String URL = "http://google.com";
	private static final long WAIT_TIMEOUT = 20; //sec

	public GoogleSearchPage(){
		//get the driver for the current thread
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@FindBy(name = "q")
	private WebElement searchBox;

	public void searchFor(String term) throws Exception {
		//wait for searchBox to appear
		new WebDriverWait(DriverFactory.getWebDriver(), WAIT_TIMEOUT)
		.until(ExpectedConditions.visibilityOf(searchBox));

		//send the text and submit it
		searchBox.sendKeys(term);
		searchBox.submit();
	}
}



public class GoogleSearchTest extends SeleniumBase {
	

	private void googleSearchTest(String term){
		try {
			//arrange - create the test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - call the search method
			gsp.searchFor(term);

			//assert - compare expected to actual
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.until(ExpectedConditions.titleContains(term))
					);
//
//			//using predicate in the until arg - cannot assert though
//			Predicate<WebDriver> titleContainsTerm = d->d.getTitle().contains(term);
//			new WebDriverWait(getWebDriver(), WAIT_TIMEOUT).until(titleContainsTerm);

			//using lambda in the wait.until arg - 
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
			.ignoring(StaleElementReferenceException.class);
			Assert.assertTrue( 
					wait.until(d->d.getTitle().contains(term))
					);

		}
		catch(Exception e) {
			Assert.assertTrue(false);
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Test(dataProvider = "getTestData")
	public void test(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] getTestData(){
		return getData();
	}
}
