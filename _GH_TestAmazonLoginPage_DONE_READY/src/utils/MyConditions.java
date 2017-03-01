package utils;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.JavascriptExecutor;

public class MyConditions {
	
	
	  /**
	   * An expectation to check if js executable
	   *
	   * Usefull when  you know, that there should be js val or something at the stage
	   *
	   * @param javaScript used as executable script
	   * @return true once javaScript executed without errors
	   */
	  public static ExpectedCondition<Boolean> javaScriptThrowsNoExceptions(
	    final String javaScript) {
	    return new ExpectedCondition<Boolean>() {
	      public Boolean apply(WebDriver driver) {
	        try {
	          ((JavascriptExecutor) driver).executeScript(javaScript);
	          return Boolean.TRUE;
	        } catch (Exception e) {
	          return Boolean.FALSE;
	        }
	      }

	      @Override
	      public String toString() {
	        return String.format("js %s to be executable", javaScript);
	      }
	    };
	  }

	  /**
	   * An expectation for String value from javascript
	   *
	   * @param javaScript as executable js line
	   * @return true once js return string
	   */
	  public static ExpectedCondition<Object> jsReturnsValue(
	    final String javaScript) {
	    return new ExpectedCondition<Object>() {
	      @Override
	      public Object apply(WebDriver driver) {
	        Object value = null;
	        try {
	          value = ((JavascriptExecutor) driver).executeScript(javaScript);
	        } catch (Exception e) {/**/}
	        if (value == null) {
	          return null;
	        }
	        if (value instanceof List) {
	          return ((List) value).isEmpty() ? null : value;
	        }
	        if (value instanceof String) {
	          return ((String) value).isEmpty() ? null : value;
	        } else {
	          return value;
	        }
	      }

	      @Override
	      public String toString() {
	        return String.format("js %s to be executable", javaScript);
	      }
	    };
	  }

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
