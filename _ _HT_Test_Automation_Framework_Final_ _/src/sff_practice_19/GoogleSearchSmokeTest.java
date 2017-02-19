package sff_practice_19;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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

import static sff_practice_19.DriverType.CHROME;
import static sff_practice_19.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME("chrome browser") {
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver", 
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX("firefox browser") {
		public WebDriver getDriver() {
			return new ChromeDriver();
		}
	};
	private String label;
	private DriverType(String s) {
		label = s;
	}
	public String getLabel() {
		return label;
	}
}

class DriverFactory {
	private DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>(){};

	@BeforeMethod
	public WebDriver createWebDriver() {
		tl.set(dt.getDriver());
		return tl.get();
	}

	@AfterMethod
	public void releaseWebDriver() {
		tl.get().quit();
		tl.remove();
	}

	public static WebDriver getWebDriver() {
		return tl.get();
	}
}


class SeleniumBase extends DriverFactory {
	protected long WAIT_TIMEOUT = 20; //sec
	private Object[][] testData = {
			{"seattle seahawks"},
			{"sounders"},
			{"storm"},

	};
	protected Object[][] getTestData(){
		return testData;
	}
}

class GoogleSearchPage {
	private String URL = "http://google.com";
	private long WAIT_TIMEOUT = 20; //sec

	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL); //oops - forgot to add this line - the only mistake!
		driver.manage().window().maximize();
	}

	@FindBy(name = "q")
	private WebElement txtSearchBox;

	public void searchFor(String term) {
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for search box to appear by proxy
		new WebDriverWait(driver, WAIT_TIMEOUT)
		.ignoring(StaleElementReferenceException.class)
		.until(ExpectedConditions.visibilityOf(txtSearchBox));

		//set the text then submit forn
		txtSearchBox.sendKeys(term);
		txtSearchBox.submit();
	}
}

public class GoogleSearchSmokeTest extends SeleniumBase {

	public void googleSearchSmokeTest(String term) {
		try {
			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - call search method
			gsp.searchFor(term);

			//assert result
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class)
					.until(ExpectedConditions.titleContains(term))
					);

			//assert result - use lambda
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(),  WAIT_TIMEOUT)
			.ignoring(StaleElementReferenceException.class);
			Assert.assertTrue(wait.until(d->d.getTitle().contains(term)));
			
			//another way 
			WebDriverWait wwait = new WebDriverWait(getWebDriver(),  WAIT_TIMEOUT);
			Assert.assertTrue(wwait.until(ExpectedConditions.titleContains(term)));
		}
		catch(Exception e) {
			Assert.assertTrue(false);
		}
	}

	@Test(dataProvider = "testDataProvider")
	public void test(String term){
		googleSearchSmokeTest(term);
	}

	@DataProvider(parallel = false)
	public Object[][] testDataProvider() {
		return getTestData();
	}

}









