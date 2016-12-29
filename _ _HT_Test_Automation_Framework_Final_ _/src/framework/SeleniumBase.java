package framework;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import framework.ExcelReader;

/**
 * 
 * @author The TestNG test cases should extend this base class
 *
 */

public abstract class SeleniumBase extends WebDriverThread {
	protected String URL;
	protected BrowserType browserType;
	protected WebDriver driver;
	protected long implicitTimeOut;
	protected String elementPath;
	protected WebElement element;
	protected JavascriptExecutor executor;
	protected ExcelReader excelReader;
	public static Logger log;
	private final static String excelFilePath = "\\testdata\\testdata.xlsx";

	protected SeleniumBase(){
		try{
			log = Log4JLogger.initLog4JLogging();
		}
		catch(FileNotFoundException e){//FileNotFoundException extends IOException
			e.getMessage();
		}
		catch(IOException e){
			e.getMessage();
		}
		catch(Exception e){
			e.getMessage();
		}
	}
	public static Object[][] getTestData() {
		int rows = 0, cols = 0;
		Object[][] testData = null;

		try{
			//load data from excel sheet and get the rows and cols so we can dim our object array
			ExcelReader reader = new ExcelReader(excelFilePath);

			try{
				rows = reader.getSheetRows("Testdata");
				cols = reader.getSheetColumns("Testdata");
			}
			catch(Exception e){
				System.out.println("SeleniumBase:getTestData - The test data file appears to be empty.");
				return null;
			}

			//get data - skip first row of headers
			testData = new Object[rows-1][cols]; 
			for(int i=0; i<cols; i++){ 
				for(int j=0; j<rows-1; j++){
					testData[j][i] = reader.getCellData("Testdata", i, j+1);
					log.debug("in getTestData()... " + testData[j][i].toString());
				}
			}	
			TestUtils.print2dArray(testData);	//debug
		}
		catch(FileNotFoundException e){
			e.getMessage();
			return null;
		}
		catch(IOException e){
			e.getMessage();
			return null;
		}
		catch(Exception e){
			e.getMessage();
			return null;
		}
		return testData;
	}
}
