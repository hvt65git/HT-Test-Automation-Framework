package sff_26;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static sff_26.DriverType.*;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME("Chrome Version 56.0.2924.87") {
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX("Firefox Version 52") {
		public WebDriver getDriver(){
			return new FirefoxDriver();
		}
	};

	private String version;

	public String getVersion() {
		return version;
	}

	private DriverType(String version) {
		this.version = version;
	}
}


class DriverFactory {
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>(){
		public WebDriver initialize() {
			return null;
		}
	};

	public static WebDriver getWebDriver() { //static so we dont have to instantiate objects
		return tl.get();
	}

	private void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}

	@BeforeMethod
	public WebDriver createWebDriver() {
		setWebDriver(CHROME.getDriver());
		return getWebDriver();
	}

	@AfterMethod
	public void releaseWebDriver() {
		getWebDriver().quit();
		tl.remove();
	}
}

class TestCase extends DriverFactory {
	protected long WAIT_TIMEOUT = 20; //sec
	private Object[][] testData;

	protected Object[][] getTestData() {
		return testData;
	}

	protected void setTestData(Object[][] testData) {
		this.testData = testData;
	}

	//init log4j etc
}

class GoogleMainSearchPage {
	//implementing page obj model here
	private long WAIT_TIMEOUT = 20; //sec
	private final String URL = "http://google.com";

	public GoogleMainSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	//@FindBy(xpath = "//*[@name = 'q']"
	@FindBy(name = "q")
	private WebElement searchBox;

	public void searchFor(final String term) throws Exception {
		//wait for searchBox to be present - by proxy since we using page obj model
		WebDriver driver = DriverFactory.getWebDriver();

		//		FluentWait<WebDriver> wait = new WebDriverWait(driver, WAIT_TIMEOUT);
		//		wait.until(ExpectedConditions.visibilityOf(searchBox));

		//		Wait<WebDriver> wait2 = new WebDriverWait(driver, WAIT_TIMEOUT);
		//		wait2.until(ExpectedConditions.visibilityOf(searchBox));

		//		
		//		new WebDriverWait(driver, WAIT_TIMEOUT)
		//		.until(ExpectedConditions.visibilityOf(searchBox));

		//send the text and submit the form
		searchBox.sendKeys(term);
		searchBox.submit();
	}
}

public class GoogleSearchSmokeTest extends TestCase {

	private void googleSearchSmokeTest(final String term) {
		try {
			//arrange - create a test object
			GoogleMainSearchPage gsp = new GoogleMainSearchPage();

			//act - invoke test object method
			gsp.searchFor(term);

			//assert (testNG) - verify test result
			//use lambda expression
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class)
					.until(d->d.getTitle().contains(term))
					);
		}
		catch(Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}


	@Test(dataProvider = "testDataProvider")
	public void test01(String term) {
		googleSearchSmokeTest(term);
	}

	@DataProvider(parallel = true) 
	public Object[][] testDataProvider() {
		setTestData(
				new Object[][]{
						{"seattle seahawks"},
						{"seattle storm"},
						{"seattle sounders"},
						{"seattle super sonics"}}
				);
		return getTestData();
	}
}
