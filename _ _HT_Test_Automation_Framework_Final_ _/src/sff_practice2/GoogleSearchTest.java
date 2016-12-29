package sff_practice2;

import org.openqa.selenium.WebDriver;

import static sff_practice2.DriverType.CHROME;
import static sff_practice2.DriverType.FIREFOX;

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

interface Driver {
	public WebDriver getWebDriver();
}

enum DriverType implements Driver{
	FIREFOX{
		public WebDriver getWebDriver(){
			return new FirefoxDriver();
		}
	},
	CHROME{
		public WebDriver getWebDriver(){
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") +
					"\\lib\\chromedriver.exe");
			return new ChromeDriver();
		}
	}
}


class DriverFactory{
	private static ThreadLocal<WebDriver> tl = 
			new ThreadLocal<WebDriver>();

	@BeforeMethod
	protected WebDriver CreateWebDriver() throws Exception{
		WebDriver driver = null;
		DriverType dt = DriverType.CHROME;//should read in from prop file
		
		switch(dt){
		case CHROME:
			driver = CHROME.getWebDriver();
			break;
		case FIREFOX:
			driver = FIREFOX.getWebDriver();
			break;
		default:
			throw new Exception("CreateWebDriver - Invalid DriverType specified ");
		}

		setWebDriver(driver);
		return getWebDriver();
	}

	protected static WebDriver getWebDriver(){
		return tl.get();
	}

	private static void setWebDriver(WebDriver d){
		tl.set(d);
	}

	@AfterMethod
	protected static void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

class SeleniumBase extends DriverFactory{
	//set properties for log4j

}
/***
 * 
 * @code GoogleSearchPage - Page Object for Google Search
 *
 */
class GoogleSearchPage{
	private String searchTerm;
	private static final long timeout = 30;//sec
	private final String URL = "http://google.com";

	public GoogleSearchPage(String term){
		searchTerm = term;
		PageFactory.initElements(DriverFactory.getWebDriver(), this);
	}

	@FindBy(name = "q") 
	WebElement searchBox;

	public void search_google() throws Exception {
		WebDriver driver = DriverFactory.getWebDriver();

		//launch google
		driver.get(URL);
		driver.manage().window().maximize();

		//wait for search box to appear by proxy
		new WebDriverWait(driver, timeout)
		.until(ExpectedConditions.visibilityOf(searchBox));

		//send search term to search box
		searchBox.sendKeys(searchTerm);

		//submit the search
		searchBox.submit();
	}
	

}

public class GoogleSearchTest extends SeleniumBase{
	private static final long timeout = 30;//sec

	private void searchGoogle(String term){
		try{
			//arrange - create test object
			GoogleSearchPage page = new GoogleSearchPage(term);
	
			//act - perform search
			page.search_google();
	
			//assert - verify search result after waiting
			//for page title to contain the search term
			Assert.assertTrue(
					new WebDriverWait(getWebDriver(),timeout)
						.until(ExpectedConditions.titleContains(term)));
		}
		catch(Exception e){
			Assert.assertTrue(false);
			System.out.println("GoogleSearchTest.searchGoogle Exception: " + e.getMessage());
		}
	}

	@Test(dataProvider = "testDataProvider")
	public void test(String term){
		searchGoogle(term);
	}

	@DataProvider(parallel = true)
	public Object[][] testDataProvider(){
		return new Object[][]{
				{"helen"},
				{"guam"},
				{"los angeles"},
				{"new york city"},
				{"plattsburgh"},
				{"florida"}
		};
	}
}
