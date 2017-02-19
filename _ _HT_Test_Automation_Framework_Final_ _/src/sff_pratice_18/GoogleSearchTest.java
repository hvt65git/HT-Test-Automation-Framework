package sff_pratice_18;

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

import static sff_pratice_18.DriverType.CHROME;
import static sff_pratice_18.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME("chrome driver version xxx") {
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX("firefox version yyy") {
		public WebDriver getDriver() {
			return new FirefoxDriver();
		}
	};
	private String label;
	private DriverType(String label){
		this.label = label;
	}
	public String getLabel(){
		return this.label;
	}
}

class DriverFactory {
	private final DriverType dt = CHROME;
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
	protected final long WAIT_TIMEOUT = 20; //secs
	protected  Object[][] testData = {
			{"patriots again"},
			{"falcons win"},
			{"yeah right"}
	};
}

class GoogleSearchPage {
	private long WAIT_TIMEOUT = 20;
	private String URL = "http://google.com";

	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@FindBy(name = "q")
	private WebElement txtSearchBox;

	public void searchFor(final String term) throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for search box to appear - use by proxy wait
		new WebDriverWait(driver, WAIT_TIMEOUT).
		until(ExpectedConditions.visibilityOf(txtSearchBox));

		//set the text then submit the search
		txtSearchBox.sendKeys(term);
		txtSearchBox.submit();
	}
}

public class GoogleSearchTest extends SeleniumBase {

	private void googleSearchTest(final String term){
		try {
			//arrange
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act
			gsp.searchFor(term);

			//assert result

			//(1) - using ExpectedConditions class method - FluentWait implementation
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
					.until(ExpectedConditions.titleContains(term))
					);

			//(2) - implementing the Function<Kin,Tout> interface  - FluentWait implementation
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.until(new Function<WebDriver, Boolean>() {
						public Boolean apply(WebDriver d) {
							return d.getTitle().contains(term);
						}
					})
					);

			//(3) - implementing the ExpectedCondtion<Out> interface which extends the Function<In, Out> interface
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
					.until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver d) {
							return d.getTitle().contains(term);
						}
					})
					);

			//(4) using Wait object and lambda expression - Wait implementation
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(),WAIT_TIMEOUT);
			Assert.assertTrue(
					wait.until(d->d.getTitle().contains(term))
					);

			//(5) using a Predicate without Assert  - FluentWait implementation
			Predicate<WebDriver> titleContainsTerm = d->d.getTitle().contains(term);
			new WebDriverWait(getWebDriver(),WAIT_TIMEOUT).
			until(titleContainsTerm);
		

		}
		catch(Exception e) {
			Assert.assertFalse(true);
			e.printStackTrace();
		}
	}

	@Test(dataProvider = "testDataProvider")
	public void test(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = false)
	public Object[][] testDataProvider(){
		return testData; 
	}
}


//MISTAKES:
//only one: had web.chrome.driver instead of webdriver.chrome.driver
//so memorize WDCD not WCD




