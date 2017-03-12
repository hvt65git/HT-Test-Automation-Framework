package sff_practice_20;

import static sff_practice_20.DriverType.CHROME; //bugbug OOPS - forgot the static!
import static sff_practice_20.DriverType.FIREFOX;

import org.omg.CORBA.INITIALIZE;
import org.openqa.selenium.StaleElementReferenceException;
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
	CHROME("chrome driver"){
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver", 
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX(""){
		public WebDriver getDriver() {
			return new FirefoxDriver();
		}
	};
	private String label;
	private DriverType(String label) {
		this.label = label;
	}

}

class DriverFactory {
	private final DriverType dt = CHROME;

	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>() {
		public WebDriver initialize() {
			return null;
		}
	};

	public static WebDriver getWebDriver() {
		return tl.get();
	}

	private void setWebDriver(WebDriver driver) {
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
	//declare global constants here
	//set properties here 
	//read in the test data from excel spreadsheets here
	protected static final long WAIT_TIMEOUT = 20; //seconds
	protected Object[][] getTestData(){
		return testData;
	}
	private Object[][] testData = {
			{"seattle seahawks"}, {"sounders"}, {"sonics"}
	};
}


class GoogleSearchPage {
	private static final long WAIT_TIMEOUT = 20; //sec
	private static final String URL = "http://google.com";

	//using POM - Page Object Model
	@FindBy(name = "q")
	WebElement txtBox;

	public GoogleSearchPage(){
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver,this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	public void searchFor(String term) throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for txtBox to appear by proxy
		//an exception will be thrown if timeout is reached
		new WebDriverWait(driver,WAIT_TIMEOUT)
		.ignoring(StaleElementReferenceException.class)
		.until(ExpectedConditions.visibilityOf(txtBox));

		//send text to txtbox
		txtBox.sendKeys(term);

		//submit the search
		txtBox.submit();
	}

}

public class GoogleSearchTest extends SeleniumBase {

	public void googleSearchTest(String term) {
		try {
			//arrange - create a test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - call the searchFor method
			gsp.searchFor(term);

			//assert - verify the result of this test
			//(1) first way - using ExpectedConditions class method
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class)
					.until(ExpectedConditions.titleContains(term))
					);

			//(2) second way - lambda expression Java 8
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(), WAIT_TIMEOUT);
			Assert.assertTrue(
					wait.until(d->d.getTitle().contains(term))
					);

			//(3) deriv of (2)- implement ExpectedCondition interface
			Assert.assertTrue(
					wait.until(new Function<WebDriver,Boolean>(){
						public Boolean apply(WebDriver d) {
							return d.getTitle().contains(term);
						}
					}));

			//(4) 
			Assert.assertTrue(
					wait.until(new ExpectedCondition<Boolean>(){
						public Boolean apply(WebDriver d) {
							return d.getTitle().contains(term);
						}
					}));

			//(5) 
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.until(new ExpectedCondition<Boolean>(){
						public Boolean apply(WebDriver d) {
							return d.getTitle().contains(term);
						}
					}));

			//(6)
			Predicate<WebDriver> titleContainsTerm = new Predicate<WebDriver>(){
				public boolean apply(WebDriver d) {
					return d.getTitle().contains(term);
				}
			};
			new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
			.until( d->d.getTitle().contains(term)); //YAYAY! WE CAN DO LAMBAS IN WEBDRIVERWAIT.UNTIL NOW IN 3.0!!!
			
			//(7)
			Predicate<WebDriver> titleContainsTerm2 = d->d.getTitle().contains(term);
			new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
			.until( d->d.getTitle().contains(term));
			
		}
		catch(Exception e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}
		finally{
			System.out.println();
		}
	}

	@Test(dataProvider = "testDataProvider")
	public void googleSmokeTest(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testDataProvider() {
		return getTestData();
	}
}




