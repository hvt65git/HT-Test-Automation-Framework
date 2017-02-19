package sff_practice_21;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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

import static sff_practice_21.DriverType.CHROME;
import static sff_practice_21.DriverType.FIREFOX;

interface Driver {
	public WebDriver getDriver();
}

enum DriverType implements Driver {
	CHROME {
		public WebDriver getDriver() {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
			return new ChromeDriver(){};
		}
	},
	FIREFOX {
		public WebDriver getDriver() {
			return new FirefoxDriver(){};
		}
	};	
}

class DriverFactory {
	private DriverType dt = CHROME;
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>() {
		public WebDriver initialize() {
			return null;
		}
		public void remove() {
			super.remove();
			System.out.println("test test test - just invoked super.remove");
		}
		public WebDriver get() {
			System.out.println("test test test - just invoked super.get.");
			return super.get();
		}
	};

	public static WebDriver getWebDriver() {
		return tl.get();
	}

	public void setWebDriver(WebDriver driver) {
		tl.set(driver);
	}

	@BeforeMethod
	public WebDriver createWebDriver() {
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
	private final Object[][] testData = { 	
			{"green prehnite"},
			{"seattle seahawks"},
			{"toronto blue jays"}
	};
	public Object[][] getTestData() {
		return testData;
	}	
}

class GoogleSearchPage {
	private final String URL = "http://google.com";
	private final long WAIT_TIMEOUT = 20;

	@FindBy(xpath = "//*[@name='q']")
	WebElement txtBox;

	public GoogleSearchPage() {
		WebDriver driver = DriverFactory.getWebDriver();
		PageFactory.initElements(driver,this);
		driver.get(URL);
		driver.manage().window().maximize();
	}

	public void searchFor(String term) throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();

		//wait for the txtBox element to appear
		new WebDriverWait(driver, WAIT_TIMEOUT)
		.ignoring(StaleElementReferenceException.class)
		.until(ExpectedConditions.visibilityOf(txtBox));

		//send the search string to the txtbox
		txtBox.sendKeys(term);
		//txtBox.sendKeys(Keys.ENTER);
		txtBox.submit();
	}

}

public class GoogleSmokeTest extends SeleniumBase {

	public void googleSmokeTest(final String term) { 

		try {
			//arrange - create a test object
			GoogleSearchPage gsp = new GoogleSearchPage();

			//act - invoke the search method
			gsp.searchFor(term);

			//assert - verify result - compare actual to expected
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class)
					.until(ExpectedConditions.titleContains(term))
					);


			//assert - verify result - compare actual to expected
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class)
					.until(
							new ExpectedCondition<Boolean>(){
								public Boolean apply(WebDriver d) {
									return d.getTitle().contains(term);
								}
							})
					);


			//assert - verify result - compare actual to expected
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class)
					.until(
							new Function<WebDriver,Boolean>(){
								public Boolean apply(WebDriver d) {
									return d.getTitle().contains(term);
								}
							})
					);
			
			//another way - using Predicate (lambda exp)
			Wait<WebDriver> wait = new WebDriverWait(getWebDriver(),WAIT_TIMEOUT);
			Assert.assertTrue(wait.until(d->d.getTitle().contains(term))
					);

			//last way with Predicate
			
		Predicate<WebDriver> titleContainsTerm = d -> d.getTitle().contains(term);
		new WebDriverWait(getWebDriver(),WAIT_TIMEOUT)
		.until(titleContainsTerm);

		}
		catch(Exception e) {
			Assert.assertTrue(false);
		}
	}

	@Test(dataProvider = "getTestData")
	public void test01(String term) {
		googleSmokeTest(term);
	}

	@DataProvider(parallel = false)
	public Object[][] getTestData() {
		return super.getTestData();
	}
}

//Note: for
//driver.findElement(By.name("q")).click();
//Click this element. 
//If this causes a new page to load, you should discard all references to this element 
//and any further operations performed on this element will throw a StaleElementReferenceException.
//Note that if click() is done by sending a native event (which is the default on most browsers/platforms) 
//then the method will _not_ wait for the next page to load and the caller should verify that themselves.
//There are some preconditions for an element to be clicked. 
//The element must be visible and it must have a height and width greater then 0.
//Throws:StaleElementReferenceException - If the element no longer exists as initially defined