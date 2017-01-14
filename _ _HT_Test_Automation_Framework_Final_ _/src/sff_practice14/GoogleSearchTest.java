package sff_practice14;

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
import com.google.common.base.Predicate;

import static sff_practice14.DriverType.CHROME;
import static sff_practice14.DriverType.FIREFOX;

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
	private static DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>() {
		public WebDriver initialize() {
			return null;
		}
	};

	@BeforeMethod
	public WebDriver createWebDriver() {
		setWebDriver(dt.getDriver());
		return getWebDriver();
	}

	private void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}

	public static WebDriver getWebDriver() {
		return tl.get();
	}

	@AfterMethod
	public void ReleaseWebDriver() {
		getWebDriver().quit();
		tl.remove();
	}

}

class SeleniumBase extends DriverFactory {
	//set props for log4j

	//set constants
	protected static final long WAIT_TIMEOUT = 20; //sec

	//init test data
	protected static final Object[][] testData = 
		{{"seattle seahawks"},
		{"sounders"},
		{"amazon.com"}};
}

class GoogleSearchPage {
	private static String URL = "http://google.com";
	private static final long WAIT_TIMEOUT = 20; //sec

	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();	
	}

	@FindBy(xpath = "//*[@name = 'q']")
	private WebElement txtBox;

	public void searchFor(final String term) throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for txtBox element to appear
		new WebDriverWait(driver, WAIT_TIMEOUT).until(
				ExpectedConditions.visibilityOf(txtBox));

		//send the text and submit the search
		txtBox.sendKeys(term);
		txtBox.submit();
	}

}

public class GoogleSearchTest extends SeleniumBase  {

	public void googleSearchTest(final String term) {

		try {
			//arrange - create the test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - call the searchFor method
			gsp.searchFor(term);
			
			//assert - verify expected value and actual value are equivalent
			
			//(1) use ExpectedCondition implementation
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
							.until(ExpectedConditions.titleContains(term))
					);
			
			//(2) use a lambda expression
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(),WAIT_TIMEOUT);
			Assert.assertTrue(wait.until(d->d.getTitle().contains(term)));
	
			//(3) use predicate but return type is void and
			//would receiyCannot cast from void to Boolean
			Predicate<WebDriver> condition = d->d.getTitle().contains(term);
			new WebDriverWait(getWebDriver(),WAIT_TIMEOUT).until(condition);
			
		}
		catch(Exception e){
			System.out.println("GoogleSearchTest.googleSearchTest Exception: "
					+ e.getMessage());
		}
	}
	
	@Test(dataProvider = "getTestData")
	public void test(String term){
		googleSearchTest(term);
	}
	
	@DataProvider(parallel = false)
	public Object[][] getTestData(){
		//load the test data from excel file into 
		//test data array and then return array
		return testData;
	}
	
}
