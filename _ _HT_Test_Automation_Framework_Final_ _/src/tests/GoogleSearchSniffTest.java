package tests;

import java.util.NoSuchElementException;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.SeleniumBase;
/**
 * 
 *  GoogleSearchSniffTest - Performs Selenium sniff test of Google search functionality
 * 								   for the browser specified by the testng.xml file
 *
 */
public class GoogleSearchSniffTest extends SeleniumBase {
	private static final String URL = "http://google.com";
	int c = 1;
	
	public void executeGoogleSearchTest(final String term){	
		try{
			System.out.println("COUNTER - " + c + "*** *** BEGIN executeGoogleSearchTest *** ***");
			
			//arrange
			System.out.println("*** " + c++ + " Before- getWebDriver() = " + getWebDriver());
			getWebDriver().get(URL);	
			
			GoogleSearchPage search_page = new GoogleSearchPage(getWebDriver());
			System.out.println("*** " + c++ + " After--- GoogleSearchPage = " + getWebDriver());
			
			//act
			Boolean result = search_page.verify_search(term);
			
			//assert
			Assert.assertTrue(result);
			
			//log
			if(result == true){
				log.debug("The search result was successful for term: " + term);
			}
			else{
				log.debug("The search result was not successful for term:  " + term);
			}
			System.out.println("COUNTER - " + c + "*** *** END executeGoogleSearchTest *** ***");
		}
		catch(NoSuchElementException nse){
			Assert.assertTrue(false);
			nse.getMessage();
		}
		catch(Exception e){
			Assert.assertTrue(false);
			e.getMessage();
		}
	}
	
	@DataProvider(parallel = true)
	public Object[][] testDataProvider() {
		return getTestData() ;
	}
	
	@Test(dataProvider = "testDataProvider")
	public void test(String term){
		executeGoogleSearchTest(term);
	}
}
//log file output
//in getTestData()... trump
//in getTestData()... clinton
//in getTestData()... election results
//in getTestData()... kung fu
//in getTestData()... kundalini
//in getTestData()... anthony wiener
//The search result was successful for term: kung fu
//The search result was successful for term: election results
//The search result was successful for term: trump
//The search result was successful for term: clinton
//The search result was successful for term: kundalini
//The search result was successful for term: anthony wiener