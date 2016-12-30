package com.amazon.Pages;

//An enum declared outside a class must NOT be marked static, final , abstract, protected , or private
public enum BrowserType {
	FIREFOX("firefox"),
	IE("ie"), 
	CHROME("chrome"),
	HTMLUNIT("html unit driver");

	private final String label;

	private BrowserType(String label) {
		this.label = label;
		System.out.println("BrowserType enum private constructor executed...");
	}

	public String getLabel() {
		return this.label;
	} 

}
