package sff_25;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GoogleSearchSmokeTest2 extends SeleniumBase {
	
	@Test(dataProvider = "dataProvider")
	public void test01(String term) {
		System.out.println("current term is: " + term);
	}
	
	@DataProvider
	public Object[][] dataProvider() {
		
		setTestData(new Object[][]{
				{"chitty chitty bang bank"},
				{"willy wonka's chocolate factory"},
				{"ciao lupo"}});
		
		return getTestData();
	}
}
