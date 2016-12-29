package tests;

import org.openqa.selenium.By;

/**
 * 
 * @author focalpt
 *
 */

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class GoogleSearchPage {
	WebDriver driver;
	Wait<WebDriver> wait;
	private static final long WAIT_TIMEOUT = 5;//seconds

	@FindBy(how=How.NAME, using="q")
	private WebElement searchbox;

	public GoogleSearchPage(WebDriver dl){
		this.driver = dl;
		wait = new WebDriverWait(dl,WAIT_TIMEOUT)
					.ignoring(StaleElementReferenceException.class);
		//note this initElements is in constructor not in calling class
		PageFactory.initElements(dl, this); 
	}

	public Boolean verify_search(String term){
		try{		
			wait.until(ExpectedConditions.visibilityOf(searchbox)); 
			searchbox.sendKeys(term);
			searchbox.submit();
		}
		catch(Exception e){
			System.out.println("Exception in verify_search: " + e.getMessage());
			return false;
		}
		return new WebDriverWait(driver, WAIT_TIMEOUT)
					.until(ExpectedConditions.titleContains(term)); 
	}
}
