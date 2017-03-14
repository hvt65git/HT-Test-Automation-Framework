package sff_practice_24;

import static sff_practice_24.DriverType.CHROME;
import static sff_practice_24.DriverType.FIREFOX;

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
	CHROME{
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir")  + "\\lib\\chromedriver.exe");
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

	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>() {
		public WebDriver initialize() {
			return null;
		}
	};

	public static WebDriver getWebDriver() {
		return tl.get();
	}

	@BeforeMethod
	public WebDriver createWebDriver() {
		tl.set(dt.getDriver());
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
	protected Object[][] testData = {
			{"seattle mariners"},
			{"seattle seahawks"},
			{"seattle sounders"},
			{"seattle storm"}
	};

	public Object[][] getTestData() {
		return testData;
	}
}

class GoogleMainPage {
	private final long WAIT_TIMEOUT = 20; //sec
	private final String googleURL = "http://google.com";
	public GoogleMainPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(googleURL);
		driver.manage().window().maximize();
	}

	@FindBy(name = "q")
	WebElement txtBox;

	public void searchFor(String term) throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();
		
		//wait for search box to appear by proxy, throw exception if it does not appear
		//(1) best way
		new WebDriverWait(driver, WAIT_TIMEOUT)
		.ignoring(StaleElementReferenceException.class)
		.until(ExpectedConditions.visibilityOf(txtBox));

		//(2)a verbose way - return the WebElement when it is visible
		//		Visibility means that the element is not only displayed but also has a height and width that is greater than 0.
		//		Parameters:element the WebElementReturns:the (same) WebElement once it is visible
		new WebDriverWait(driver, WAIT_TIMEOUT)
		.ignoring(StaleElementReferenceException.class)
		.until( new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				return (txtBox.isDisplayed()) ? txtBox : null  ;
			}
		});
		
		//once we have WebElement we can send the txt and submit the form
		txtBox.sendKeys(term);
		txtBox.submit();
		
	}
}

public class GoogleSmokeTest extends SeleniumBase {

	public void googleSmokeTest(String term) {
		try {
			//arrange - create the test object
			GoogleMainPage gmp = new GoogleMainPage();

			//act - invoke the search method
			gmp.searchFor(term);

			//(1) assert - verify result
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class)
					.until(ExpectedConditions.titleContains(term))
					);

			//(2) - implement the Function<Kin, Tout> interface
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class)
					.until(	new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver d) {
							return d.getTitle().contains(term);
						}
					})
					);

			//(3) - java 8 lambda expression
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
			.ignoring(StaleElementReferenceException.class);

			Assert.assertTrue(
					wait.until(d->d.getTitle().contains(term))
					);
			
			//(4) Predicate - cant assert because return is not a Boolean type
			Predicate<WebDriver> titleContains = d->d.getTitle().contains(term);
			new WebDriverWait(getWebDriver(), WAIT_TIMEOUT).until(d->d.getTitle().contains(term));

		}
		catch(Exception e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}
	}

	@Test(dataProvider = "testDataProvider")
	public void test(String term) {
		googleSmokeTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testDataProvider() {
		return getTestData();
	}
}
