package sff_practice_17;

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

import static sff_practice_17.DriverType.CHROME;
import static sff_practice_17.DriverType.FIREFOX;

interface Driver {
	WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME{
		public WebDriver getDriver() {			
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");	
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
	private DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = new ThreadLocal();

	@BeforeMethod
	public WebDriver createWebDriver() throws Exception {
		setWebDriver(dt.getDriver());
		return getWebDriver();
	}

	private void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}

	public static WebDriver getWebDriver(){
		return tl.get();
	}

	@AfterMethod
	public void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory {
	//init instance variables
	protected final long WAIT_TIMEOUT = 20; //seconds
	private Object[][] testData = {
			{"seattle sonics"},
			{"seattle storm"},
			{"seattle seahawks"},
			{"seattle sounders"}
	};
	protected Object[][] getTestData(){
		return testData;
	}
	//set props for log4j
}

class GoogleSearchPage {
	private final long WAIT_TIMEOUT = 20; //seconds
	private final String URL = "http://google.com";

	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@FindBy(name = "q")
	WebElement element;

	public void searchForTerm(String term) throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for element to appear
		new WebDriverWait(driver, WAIT_TIMEOUT).
		until(ExpectedConditions.visibilityOf(element));

		//set the text and submit the form
		element.sendKeys(term);
		element.submit();
	}

}


public class GoogleSearchTest extends SeleniumBase {
	
	@DataProvider(parallel = true)
	public Object[][] testDataProvider() {
		return getTestData();
	}
	
	@Test(dataProvider = "testDataProvider")
	public void test(String term) {
		googleSearchTest(term);
	}

	private void googleSearchTest(final String term) {
		try {
			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act
			gsp.searchForTerm(term);

			//assert - verify test
			//(1) use ExpectedConditions class method
			//note:
			//<Boolean> Boolean org.openqa.selenium.support.ui.FluentWait.until(Function<? super WebDriver, Boolean> isTrue)
			//we do get a Boolean return and we are using: FluentWait.until(Function<? super WebDriver, Boolean> isTrue)
			Assert.assertTrue(new WebDriverWait(getWebDriver(), WAIT_TIMEOUT)
			.until(ExpectedConditions.titleContains(term)));

			//(2) or - implement generic interface: Function<WebDriver, Boolean> - Function<in, out>
			//using anon innerclass
			//note:
			//<Boolean> Boolean org.openqa.selenium.support.ui.FluentWait.until(Function<? super WebDriver, Boolean> isTrue)
			//we do get a Boolean return and we are using: FluentWait.until(Function<? super WebDriver, Boolean> isTrue)
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT).until( 
							new Function<WebDriver, Boolean>(){
								public Boolean apply(WebDriver d) {
									return d.getTitle().contains(term);
								}
							})
					);

			//(3) or - use lambda expression with a wait 
			//note:
			//<Boolean> Boolean org.openqa.selenium.support.ui.Wait.until(Function<? super WebDriver, Boolean> isTrue)
			//we do get a Boolean return and we are using: Wait.until(Function<? super WebDriver, Boolean> isTrue)
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(), WAIT_TIMEOUT);
			Assert.assertTrue(
					wait.until(d->d.getTitle().contains(term))
					);
			
			//(4) Predicate but cannot use Assert since return is not a Boolean
			//note: void org.openqa.selenium.support.ui.FluentWait.until(Predicate<WebDriver> isTrue)
			Predicate<WebDriver> titleContainsTerm = d->d.getTitle().contains(term);
			new WebDriverWait(getWebDriver(), WAIT_TIMEOUT).until(titleContainsTerm);
					


		}
		catch(Exception e) {
			Assert.assertTrue(false);
			System.out.println(e.getMessage());
		}
	}

}






