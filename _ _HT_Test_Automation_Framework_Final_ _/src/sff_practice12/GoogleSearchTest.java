package sff_practice12;

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

import com.google.common.base.Predicate;

import static sff_practice12.DriverType.CHROME;
import static sff_practice12.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME {
		public WebDriver getDriver(){

			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") +
					"\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX {
		public WebDriver getDriver(){
			return new FirefoxDriver();
		}
	}
};

class DriverFactory {
	private static DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = 
			new ThreadLocal<WebDriver>();

	@BeforeMethod
	public WebDriver createWebDriver() throws IllegalAccessError {
		WebDriver driver = null;

		switch(dt){
		case CHROME:
			driver = CHROME.getDriver();
			break;
		case FIREFOX:
			driver = FIREFOX.getDriver();
			break;
		default:
			throw new IllegalAccessError("DriverFactory.createWebDriver: "
					+ "Invalid driver type specified.");
		}
		setWebDriver(driver);
		return getWebDriver();
	}

	public static WebDriver getWebDriver() {
		return tl.get();
	}

	public void setWebDriver(WebDriver driver){
		tl.set(driver);
	}

	@AfterMethod
	public void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory {
	public static final long WAIT_TIMEOUT = 30; //sec
	public static final Object[][] testData = {
		{"seattle seahawks"},
		{"seattle sounders"},
		{"seattle sonics"},
		{"devin hester"}
	};
}


class GoogleSearchPage {
	private static long WAIT_TIMEOUT = 30; //secs
	private static String URL = "http://google.com";

	public GoogleSearchPage(){
		//BUGBUG
		//TRICKY TRICKY TRICKY DO NOT PUT WAIT_TIMEOUT HERE LIKE THIS, USE THIS INSTEAD!
		//PageFactory.initElements(DriverFactory.getWebDriver(), WAIT_TIMEOUT);
		PageFactory.initElements(DriverFactory.getWebDriver(), this);
		DriverFactory.getWebDriver().get(URL);
		DriverFactory.getWebDriver().manage().window().maximize();
	}

	@FindBy(name = "q")
	private WebElement txtBox;

	public void searchFor(final String term) throws Exception {
		//wait for txtBox element to appear
		new WebDriverWait(DriverFactory.getWebDriver(), WAIT_TIMEOUT).
		until(ExpectedConditions.visibilityOf(txtBox));

		//send the text and search
		txtBox.sendKeys(term);
		txtBox.submit();	
	}
}

public class GoogleSearchTest extends SeleniumBase {

	private void googleSearchTest(String term) {
		try {
			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - call search method
			gsp.searchFor(term);

			//assert - verify result
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
					.until(ExpectedConditions.titleContains(term)));

			//or- THIS WORKED!
			Wait<WebDriver> wait =
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT);
			wait.until((d)->d.getTitle().contains(term));


			//nope
			//The method until(Predicate<WebDriver>) is ambiguous for the type WebDriverWait
			//new WebDriverWait(getWebDriver(),WAIT_TIMEOUT).until((d)->d.getTitle().contains(term));


			//or- THIS WORKED!
			Predicate<WebDriver> titleContainsTerm = (d)->d.getTitle().contains(term);
			new WebDriverWait(getWebDriver(),WAIT_TIMEOUT).until(titleContainsTerm);

		}
		catch(Exception e) {
			System.out.println("GoogleSearchTest.googleSearchTest:"
					+ " Exception occurred: " + e.getMessage());
			Assert.assertTrue(false);
		}


	}

	@Test(dataProvider = "testDataProvider")
	public void test(String term) {
		googleSearchTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testDataProvider(){
		return testData;
	}
}















