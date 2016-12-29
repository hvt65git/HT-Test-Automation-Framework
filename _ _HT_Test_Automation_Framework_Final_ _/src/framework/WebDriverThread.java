package framework;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

/***
 * 
 * WebDriverThread - performs thread management for newly created driver objects of the WebDriver class
 *
 */
public class WebDriverThread {
	private static final long WAIT_TIMEOUT = 5;//seconds
	private static ThreadLocal<WebDriver> tl = new ThreadLocal<WebDriver>();

	@BeforeMethod
	@Parameters(value = {"suite-param"})
	public WebDriver initWebDriver(String value) throws IllegalAccessException{
		BrowserType bt = null;
		switch(value){
		case "CHROME":
			bt = BrowserType.CHROME;
			break;
		case "FIREFOX":
			bt = BrowserType.FIREFOX;
			break;
		default:
			throw new IllegalAccessException("In initWebDriver. Error - Undefined BrowserType");
		};

		//WebDriver driver = new WebDriverFactory().create(bt);
		WebDriver driver =  WebDriverFactory.create(bt);  //try single static WebDriverFactory object
		setWebDriver(driver);
		
		getWebDriver().manage().timeouts().implicitlyWait(WAIT_TIMEOUT, TimeUnit.SECONDS);
		getWebDriver().manage().window().maximize();
		
		return getWebDriver();
	}

	private void setWebDriver(WebDriver driver){
		tl.set(driver);
	}

	public WebDriver getWebDriver(){
		return tl.get();
	}

	@AfterMethod
	public void releaseWebDriver(){
		getWebDriver().quit();
		tl.remove();
	}
}

//note:
//@BeforeTest and @AfterTest - are  only for the test tags in the testng.xml file
