package sff_practice15;


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

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import static sff_practice15.DriverType.CHROME;
import static sff_practice15.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME{
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver", 
					System.getProperty("user.dir") + 
					"\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX{
		public WebDriver getDriver() {
			return new FirefoxDriver();
		}
	}
}

class DriverFactory {
	private static final DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = 
							new ThreadLocal<WebDriver>();
	
	@BeforeMethod
	public WebDriver createWebDriver(){
		WebDriver driver = dt.getDriver();
		setWebDriver(driver);
		return getWebDriver();
	}

	@AfterMethod
	public void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}

	private void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}

	public static WebDriver getWebDriver() {
		return tl.get();
	}
}

class SeleniumBase extends DriverFactory {
	protected static final long WAIT_TIMEOUT = 20; //seconds
	private Object[][] testData = {
			{"hello world"}, 
			{"seattle sounders"},
			{"seahawks"}
	};
	protected Object[][] getTestData(){
		return testData;
	}
}

class GoogleSearchPage {
	private static final String URL = "http://google.com";
	private static final long WAIT_TIMEOUT = 20; //seconds

	@FindBy(name = "q")
	WebElement txtSearch;

	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	public void googleSearch(String term){
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for txtSearch to appear
		new WebDriverWait(driver, WAIT_TIMEOUT)
		.until(ExpectedConditions.visibilityOf(txtSearch));

		//send the text and execute search
		txtSearch.sendKeys(term);
		txtSearch.submit();
	}                                                                                                     
}

public class GoogleSearchTest extends SeleniumBase {

	private void googleSearchTest(final String term) {
		//arrange - create test object
		GoogleSearchPage gsp = new GoogleSearchPage();

		//act - call the search method
		gsp.googleSearch(term);

		//assert - using ExpectedConditions method 
		Assert.assertTrue(
				new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
				.until(ExpectedConditions.titleContains(term))
				);

		//assert - using lambda expression and Wait obj ref
		Wait<WebDriver> wait = new WebDriverWait(getWebDriver(), WAIT_TIMEOUT);
		Assert.assertTrue(
				wait.until(d->d.getTitle().contains(term))
				);		

		//no assert - using Predicate obj ref
		Predicate<WebDriver> condition = d->d.getTitle().contains(term);
		new WebDriverWait(getWebDriver(), WAIT_TIMEOUT).until(d->d.getTitle().contains(term));
	}

	@Test(dataProvider = "testDataProvider")
	public void test(String term) {
		googleSearchTest(term);
	}
	
	@DataProvider(parallel = true)
	public Object[][] testDataProvider() {
		return getTestData();
	}
}










