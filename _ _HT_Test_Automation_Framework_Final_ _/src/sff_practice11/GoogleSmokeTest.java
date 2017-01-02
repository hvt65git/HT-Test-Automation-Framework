package sff_practice11;

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

import static sff_practice11.DriverType.CHROME;
import static sff_practice11.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME {
		public WebDriver getDriver(){
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	},
	FIREFOX {
		public WebDriver getDriver(){
			return new FirefoxDriver();
		}
	}	
}

class DriverFactory {
	private static final DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = 
			new ThreadLocal<WebDriver>();

	@BeforeMethod
	public WebDriver createWebDriver() throws Exception{
		WebDriver driver = null;

		switch(dt){
		case CHROME:
			driver = CHROME.getDriver();
			break;
		case FIREFOX:
			driver = FIREFOX.getDriver();
			break;
		default:
			throw 
			new IllegalAccessException("createWebDriver - Invalid DriverType");
		}

		setWebDriver(driver);
		return getWebDriver();
	}

	private void setWebDriver (WebDriver driver){
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
	public static final long WAIT_TIMEOUT = 20; //sec
	//set properties for log4j
}

class GoogleSearchPage {
	public static final String URL = "http://google.com";

	public GoogleSearchPage(){
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver, this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@FindBy(name = "q")
	WebElement txtBox;

	public void search(final String term) throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for txtbox to appear
		new WebDriverWait(DriverFactory.getWebDriver(),20)
		.until(ExpectedConditions.visibilityOf(txtBox));

		//set the text and perform the search
		txtBox.sendKeys(term);
		txtBox.submit();
	}
}

public class GoogleSmokeTest extends SeleniumBase {

	public void googleSmokeTest(final String term) {
		try{

			//arrange - create test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - perform a search
			gsp.search(term);

			//assert - verify results page title contains search term
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),30)
					.until(ExpectedConditions.titleContains(term))
					);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	@Test(dataProvider = "testDataProvider")
	public void test(String term){
		googleSmokeTest(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testDataProvider(){
		return new Object[][]{
				{"seattle seahawks"},
				{"seatte sounders"},
				{"tom brady"},
				{"dallas cowboys"}
		};
	}

}
