package sff_practice_22;

import static sff_practice_22.DriverType.CHROME;
import static sff_practice_22.DriverType.FIREFOX;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
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

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME{
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver"
					,System.getProperty("user.dir") + "\\lib\\chromedriver.exe"
					);
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
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>(){};
	private DriverType dt = DriverType.CHROME;	

	public static WebDriver getWebDriver() {
		return tl.get();
	}

	public void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}

	@BeforeMethod
	public WebDriver createWebDriver() {
		setWebDriver(dt.getDriver());
		return getWebDriver();
	}

	@AfterMethod
	public void releaseWebDriver() {
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory {
	protected final long WAIT_TIMEOUT = 20; //sec
	private Object[][] testData = {
			{"seattle storm"},
			{"seattle sounder"},
			{"seattle mariners"}
	};
	protected Object[][] getTestData() {
		return testData;
	}
}

class GoogleSearchPage {
	private String URL = "http://google.com";
	protected final long WAIT_TIMEOUT = 20; //sec

	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver,this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@FindBy(name = "q")
	WebElement searchBox;

	public void searchFor(String term) throws Exception {
		//get driver for current thread
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for the searchBox element to appear
		new WebDriverWait(driver,WAIT_TIMEOUT)
		.ignoring(StaleElementReferenceException.class)
		.until(ExpectedConditions.visibilityOf(searchBox));

		//send the text and submit the search
		searchBox.sendKeys(term);
		searchBox.submit();
	}
}



public class GoogleSmokeTest extends SeleniumBase {

	@Test(dataProvider = "testDataProvider")
	public void googleTest(String term){
		googleSmokeTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testDataProvider(){
		return getTestData();
	}

	private void googleSmokeTest(final String term) {
		try{
			//arrange - create a test object
			GoogleSearchPage gsp = new GoogleSearchPage(){
				public void searchFor(String term) throws Exception {
					System.out.println("in gsp anonymous inner class calling super.searchFor!");
					super.searchFor(term);	
				}
			};

			//act - all the searchFor method
			System.out.println("gsp.searchFor(term)...");
			gsp.searchFor(term);

			//assert - verify result 
			//(1) - using ExpectedConditions class method Boolean titleContains(String term)
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class)
					.until(ExpectedConditions.titleContains(term))
					);

			//(2) - implement ExpectedCondition interface's apply method
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.until(new ExpectedCondition<Boolean>(){
						public Boolean apply(WebDriver d) {
							return d.getTitle().contains(term);
						}
					}));

			//(3) - implement Function<Kin,Tout> interface's Tout apply(Kin)  method
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.until(new Function<WebDriver, Boolean>(){
						public Boolean apply(WebDriver d) {
							return d.getTitle().contains(term);
						}
					}));

			//(4) - use Wait<Webdriver> object and lamda expression in the until method argument
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(),WAIT_TIMEOUT);
			Assert.assertTrue(
					wait.until(d->d.getTitle().contains(term))
					);

			//(5) - use Predicate<WebDriver> object
			Predicate<WebDriver> titleContainsTerm = d->d.getTitle().contains(term);
			new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
			.until(d->d.getTitle().contains(term));
		}
		catch(Exception e) {
			Assert.assertTrue(false);
			System.out.println(e.getMessage());
		}
	}

}















