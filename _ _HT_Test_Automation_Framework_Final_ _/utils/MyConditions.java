package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class MyConditions {

	/**
	 * An expectation for checking that an element, known to be present on the DOM of a page, is
	 * visible. Visibility means that the element is not only displayed but also has a height and
	 * width that is greater than 0.
	 *
	 * @param element the WebElement
	 * @return the (same) WebElement once it is visible
	 */
	public static ExpectedCondition<WebElement> visibilityOf(final WebElement element) {
		return new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				return elementIfVisible(element);
			}

			@Override
			public String toString() {
				return "visibility of " + element;
			}
		};
	}

	/**
	 * @return the given element if it is visible and has non-zero size, otherwise null.
	 */
	private static WebElement elementIfVisible(WebElement element) {
		return element.isDisplayed() ? element : null;
	}


}
