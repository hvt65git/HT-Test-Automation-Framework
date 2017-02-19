package utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

public class Helpers {

	/**
	 * 
http://darrellgrainger.blogspot.com/2012/06/StaleElementReferenceException.html

QA & Testing

A technical blog about QA & testing

Wednesday, June 20, 2012
StaleElementReferenceException
Most automation tools depend on the concept of the page has finished loading.
With AJAX and Web 2.0 this has become a grey area. 
META tags can refresh the page and Javascript can update the DOM at regular intervals.

For Selenium this means that StaleElementReferenceException can occur. 
StaleElementReferenceException occurs if I find an element, 
the DOM gets updated then I try to interact with the element.

Actions like:

    driver.findElement(By.id("foo")).click();

are not atomic. Just because it was all entered on one line, the code generated is no different than:

    By fooID = By.id("foo");
    WebElement foo = driver.findElement(fooID);
    foo.click();

If Javascript updates the page between the findElement call and the click call then I'll get a StaleElementReferenceException. It is not uncommon for this to occur on modern web pages. It will not happen consistently however. The timing has to be just right for this bug to occur.

Generally speaking, if you know the page has Javascript which automatically updates the DOM, you should assume a StaleElementReferenceException will occur. It might not occur when you are writing the test or running it on your local machine but it will happen. Often it will happen after you have 5000 test cases and haven't touched this code for over a year. Like most developers, if it worked yesterday and stopped working today you'll look at what you changed recently and never find this bug.

So how do I handle it? I use the following click method:
	 */

	public static String getValue(final WebDriver driver, final By locator)
	{
		Wait<WebDriver> wait = new FluentWait(driver)
		.withTimeout(30, TimeUnit.SECONDS)
		.pollingEvery(1, TimeUnit.SECONDS)
		.ignoring(StaleElementReferenceException.class);

		String value = wait.until(new ExpectedCondition<String>() {
			public String apply(WebDriver driver) {
				return driver.findElement(locator).getAttribute("value");
			}
		});
		return value;
	}

}
