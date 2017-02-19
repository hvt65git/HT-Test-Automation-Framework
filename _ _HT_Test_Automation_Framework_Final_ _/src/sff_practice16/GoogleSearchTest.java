package sff_practice16;

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

import static sff_practice16.DriverType.CHROME;
import static sff_practice16.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME{
		public WebDriver getDriver(){
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX{
		public WebDriver getDriver(){
			return new FirefoxDriver();
		}
	}
}

class DriverFactory {
	private static final DriverType dt = CHROME;
	private static final ThreadLocal<WebDriver> tl = 
			new ThreadLocal<WebDriver>() {
		public WebDriver initialize(){
			return null;
		}
	};

	@BeforeMethod
	public WebDriver createWebDriver(){
		WebDriver driver = dt.getDriver();
		tl.set(driver);
		return tl.get();
	}

	public static WebDriver getWebDriver(){
		return tl.get();
	}

	private void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}

	@AfterMethod
	public void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory {
	protected static final long WAIT_TIMEOUT = 20; //secs
	private Object[][] testData = {
			{"seattle seahawks"},
			{"seattle sounder"}, 
			{"Streets of San Francisco"}
	};
	protected Object[][] getTestData(){
		return testData;
	}
}

class GoogleSearchPage {
	private  final String URL = "http://google.com";
	private  final long WAIT_TIMEOUT = 20; //secs

	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@FindBy(xpath = "//*[@name='q']")
	private WebElement txtBox;

	public  void searchFor(String term) throws Exception {
		//wait for txt box to appear - being done by proxy
		WebDriver driver = DriverFactory.getWebDriver();
		new WebDriverWait(driver,WAIT_TIMEOUT).until(ExpectedConditions.visibilityOf(txtBox));

		//send the text
		txtBox.sendKeys(term);
		txtBox.submit();
	}
}

public class GoogleSearchTest extends SeleniumBase {

	private void googleSearchTest(String term) {

		try {
			//arrange - create a test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act
			gsp.searchFor(term);

			//(1) assert - implementing the Function<In, Out> interface
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT).
					until(
							new Function<WebDriver, Boolean>() {
								public Boolean apply(WebDriver d) {
									return d.getTitle().contains(term);
								}
							}
							));

			//(2) assert - using ExpectedConditions method
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(), WAIT_TIMEOUT).
					until(ExpectedConditions.titleContains(term))
					);

			//(3) assert - or using lambda expression
			//T org.openqa.selenium.support.ui.Wait.until(Function<? super F, T> isTrue)
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(), WAIT_TIMEOUT);
			Assert.assertTrue(wait.until(d->d.getTitle().contains(term)));
		}
		catch(Exception e) {
			Assert.assertTrue(false);
			System.out.println(e.getMessage());
		}
	}

	@Test(dataProvider = "testDataProvider")
	public void test(String term){
		googleSearchTest(term);
	}

	@DataProvider(parallel = false)
	public Object[][] testDataProvider(){
		return getTestData();
	}

}
