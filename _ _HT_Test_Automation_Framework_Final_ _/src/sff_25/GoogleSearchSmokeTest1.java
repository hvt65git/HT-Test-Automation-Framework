package sff_25;

import java.util.List;

import org.openqa.selenium.By;
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


import static sff_25.DriverType.CHROME;
import static sff_25.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
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
	private final DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>(){
		public WebDriver initialize() {
			return null;
		}
	};

	public static WebDriver getWebDriver() {
		return tl.get();
	}

	private void setWebDriver(WebDriver driver) throws Exception {
		tl.set(driver);
	}

	@BeforeMethod
	public WebDriver createWebDriver() throws Exception {
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
	private Object[][] testData;

	protected void setTestData(Object[][] testData) {
		this.testData = testData;
	}

	protected Object[][] getTestData() {
		return testData;
	}
}

class GoogleSearchPage {
	private static final long WAIT_TIME_OUT = 20; //sec
	private static final String URL = "http://google.com";

	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}
	

	@FindBy(xpath = "//*[@name = 'q']")
	WebElement txtbox;

	public void searchFor(String term) throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for txtbox to appear by proxy, throws Exception if time out is reached
		new WebDriverWait(driver, WAIT_TIME_OUT)
		.until(ExpectedConditions.visibilityOf(txtbox));

		//set the text and submit it
		txtbox.sendKeys(term);
		txtbox.submit();
		

	}
}

public class GoogleSearchSmokeTest1 extends SeleniumBase {

	public void googleSearchSmokeTest1(String term) {
		
		try {

		//arrange - create a test object
		GoogleSearchPage gsp = new GoogleSearchPage();

		//action - call object's search method
		gsp.searchFor(term);

		//assert - verify result
		Assert.assertTrue(
				new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
				.until(d->d.getTitle().contains(term))
				);
		
		//another way to do this but not as DRY!
		Assert.assertTrue(
				new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
				.until(ExpectedConditions.titleContains(term))
				);
		}
		catch(Exception e) {
			Assert.assertTrue(false);
		}
	}



	@Test(dataProvider = "dataProvider")
	public void test01(String term) {
		System.out.println("current term is: " + term);
		googleSearchSmokeTest1(term);
	}

	@DataProvider
	public Object[][] dataProvider() {
		setTestData(new Object[][]{
				{"seattle"},
				{"seattle space needle"},
				{"seattle mariners"}});
		return getTestData();
	}
}












