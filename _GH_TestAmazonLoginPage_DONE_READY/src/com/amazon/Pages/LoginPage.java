/**
 * 
 */
package com.amazon.Pages;

import java.util.NoSuchElementException;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
	WebDriver driver;
	Wait<WebDriver> wait;

	public LoginPage(WebDriver dl){
		this.driver = dl;
		wait = new WebDriverWait(this.driver,18);
	}

	@FindBy(xpath = "//span[text()='Hello. Sign in']")
	WebElement sign_in_link;

	@FindBy(xpath = "//*[@id='ap_email']")
	WebElement username;

	@FindBy(how=How.XPATH, using="//*[@id='ap_password']")
	WebElement password;

	public void login_amazon(String user, String pass) throws TimeoutException, Exception{
		sign_in_link.click();
		
		wait.until(ExpectedConditions.visibilityOf(username)); 
		System.out.println("*** login_amazon - ExpectedConditions.visibilityOf... success!");
		
		username.sendKeys(user);
		password.sendKeys(pass);
		password.submit();
	}
}
