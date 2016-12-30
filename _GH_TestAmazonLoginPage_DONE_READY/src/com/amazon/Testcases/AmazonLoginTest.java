package com.amazon.Testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;





import com.amazon.Pages.LoginPage;

import utils.TestUtils;

/***
 * 
 * @author AmazonLoginTest - verifyValidLogin - logs into amazon and verifies
 * 							 that the resultant amazon main page is displayed
 *
 */

public class AmazonLoginTest {
	
	public void verifyValidLogin(WebDriver driver) {
		try{ 
			//create page object using PageFactory
			LoginPage login_page = PageFactory.initElements(driver, LoginPage.class);

			//call the method
			login_page.login_amazon(
					TestUtils.getBase64DecodedString(TestUtils.getPropVal("username")), 
					TestUtils.getBase64DecodedString(TestUtils.getPropVal("password")));


			//wait for WebELment to be found on the displayed amazon main page
			String xpath = "//a[text()= 'Hi, Henry']";
			
			//use lambda expression available in Java 8
			//new WebDriverWait(driver,30).until((WebDriver d)->d.findElement(By.xpath(xpath)));
			
			//or could do it this way too:
			Wait<WebDriver> wait = new WebDriverWait(driver,30);
			wait.until(d->d.findElement(By.xpath(xpath)));
		}
		catch(TimeoutException e){
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}

}
