package utils;

import java.util.Base64;
import java.util.List;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.openqa.selenium.JavascriptExecutor;

public class TestUtils {

	public TestUtils(){
		System.out.println("** IN TestUtils constructor");
	}	

	public static void print2dArray(Object[][] matrix){
		for (int i = 0; i < matrix.length; i++) {
			Object[] row = matrix[i];
			for (int j = 0; j < row.length; j++) {
				System.out.println("row[" + i + "]col[" + j + "] = " + matrix[i][j]); 
			}
		}
	}

	public static boolean isInList(List<String> list, String item){
		for(String current : list){
			if(item.contains(current)){
				return true;
			}
		}
		return false;
	}

	public static void waitForPageToLoad2(WebDriver driver) {
		//no! refactor this to be an ExpectedCondition implementation
		//so that we can use it with WebDriverWait and we can get a timeout
		JavascriptExecutor js = null;
		String pageLoadStatus = null;
		do {
			js = (JavascriptExecutor) driver;
			pageLoadStatus = (String)js.executeScript("return document.readyState");
			switch (pageLoadStatus) {
			case "loading":
				System.out.print("Loading: the document is still loading...");
				break;
			case "interactive":
				System.out.print("Interactive: the document has finished parsing but still loading sub-resources...");
				break;
			case "complete":
				System.out.println("Complete: the document has loaded!");
				break;
			}
		}
		while (!pageLoadStatus.equals("complete"));

	}

	public static void waitForPageToLoad(WebDriver driver) {
		JavascriptExecutor js = null;
		String pageLoadStatus = null;
		do {
			js = (JavascriptExecutor) driver;
			pageLoadStatus = (String)js.executeScript("return document.readyState");
			System.out.print(".");
		}
		while ( !pageLoadStatus.equals("complete") );
		System.out.println("Page Loaded.");
	}

	public static void scrollDown(WebDriver driver, int horiz, int vertical){
		((JavascriptExecutor)driver).executeScript("scroll(" + horiz + "," + vertical + ")");
	}

	public static String getPropVal(String key){
		Properties property = new Properties();
		FileInputStream fs;
		String value = null;

		try{
			fs = new FileInputStream(System.getProperty("user.dir")+"\\props\\user.properties");
			property.load(fs);
			value = property.getProperty(key);
		}
		catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		return value;
	}

	public static String getBase64EncodedString(String notEncoded){
		return new String(Base64.getEncoder().encode(notEncoded.getBytes()));
	};

	public static String getBase64DecodedString(String encoded){
		return new String(Base64.getDecoder().decode(encoded.getBytes()));
	};

	public static WebElement getElementUsingDynamicWait(WebDriver dr, String xpath) {
		WebElement element = null;

		if(TestUtils.isWebElementPresent(dr, By.xpath(xpath))){
			element = dr.findElement(By.xpath(xpath));
		}
		return element;
	}

	public static void findElementUsingDynamicWaitThenClick(WebDriver dr, String xpath) throws Exception{
		if(TestUtils.isWebElementPresent(dr, By.xpath(xpath))){
			dr.findElement(By.xpath(xpath)).click();
		}
		else{
			Assert.assertTrue(false);
			throw new Exception("Could not find element: " + xpath);
		}
	}

	public static boolean isWebElementPresent(WebDriver driver, By byExpression) {
		return isWebElementPresent(driver, byExpression, 3);
	}

	public static boolean isWebElementPresent(WebDriver driver, By byExpression, long seconds) {
		WebDriverWait wait = new WebDriverWait(driver, seconds);
		try { 
			wait.until(ExpectedConditions.presenceOfElementLocated(byExpression));
			return true;
		} 
		catch(TimeoutException e) {
			System.out.println(e.getMessage() + "returning false...");
			return false;
		}

	}

}
