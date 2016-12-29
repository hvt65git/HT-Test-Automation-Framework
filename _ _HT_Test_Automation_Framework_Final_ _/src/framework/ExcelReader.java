
package framework ;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author focalpt
 *
 */

public class ExcelReader {
	public FileInputStream fis =null;
	public FileOutputStream fileOut = null;
	private XSSFWorkbook workbook = null;
	private XSSFSheet sheet = null;
	private XSSFRow row = null;
	private XSSFCell cell = null;
	String path = null;

	//constructor
	public ExcelReader(String filePath) throws FileNotFoundException, IOException {
		path = System.getProperty("user.dir") + filePath;
		fis = new FileInputStream(path);

		//points to the workbook objec
		workbook = new XSSFWorkbook(fis);

		//now point to the first workbook sheet
		sheet = workbook.getSheetAt(0);
	}

	//note: need to shift left after deleting rows or columns to get changes to take effect                  
	public int getSheetRows(String sheetName){
		int index = workbook.getSheetIndex(sheetName);
		sheet = workbook.getSheetAt(index);
		return (sheet.getLastRowNum() + 1);
	}
	
	//provides the total number of columns in a sheet - test case
	public int getSheetColumns(String sheetName){
		int index = workbook.getSheetIndex(sheetName);
		sheet = workbook.getSheetAt(index);
		row = sheet.getRow(0);
		return(row.getLastCellNum());
	}

	//Provide cell value - testdata
	public String getCellData(String sheetName, int colNum, int rowNum){
		int index = workbook.getSheetIndex(sheetName);
		sheet = workbook.getSheetAt(index);
		row = sheet.getRow(rowNum);
		cell = row.getCell(colNum);
		return(cell.getStringCellValue());
	}

	//Provide cell value - testdata
	public String getCellData(String sheetName, String colName, int rowNum){
		int colNum =-1;
		int index = workbook.getSheetIndex(sheetName);
		sheet = workbook.getSheetAt(index);
		for(int i=0; i<getSheetColumns(sheetName); i++){
			row = sheet.getRow(0);
			cell = row.getCell(i);
			if(cell.getStringCellValue().equals(colName)){
				colNum = cell.getColumnIndex();
				break;
			}
		}
		row = sheet.getRow(rowNum);
		cell = row.getCell(colNum);
		return(cell.getStringCellValue());
	}

	public void setCellData(String sheetName, int colNum, int rowNum, String str){

		try {
			int index = workbook.getSheetIndex(sheetName);
			sheet = workbook.getSheetAt(index);
			
			row = sheet.getRow(rowNum);
			cell = row.createCell(colNum);//problem is here! NullPointerException
			
			cell.setCellValue(str);

			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();
		}  
		catch(NullPointerException e){
			e.getMessage();
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}


}


//
//
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Iterator;
//
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//public class ReadWriteExcelFile {
//
//	public static void readXLSFile() throws IOException
//	{
//		InputStream ExcelFileToRead = new FileInputStream("C:/Test.xls");
//		HSSFWorkbook wb = new HSSFWorkbook(ExcelFileToRead);
//
//		HSSFSheet sheet=wb.getSheetAt(0);
//		HSSFRow row; 
//		HSSFCell cell;
//
//		Iterator rows = sheet.rowIterator();
//
//		while (rows.hasNext())
//		{
//			row=(HSSFRow) rows.next();
//			Iterator cells = row.cellIterator();
//			
//			while (cells.hasNext())
//			{
//				cell=(HSSFCell) cells.next();
//		
//				if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
//				{
//					System.out.print(cell.getStringCellValue()+" ");
//				}
//				else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
//				{
//					System.out.print(cell.getNumericCellValue()+" ");
//				}
//				else
//				{
//					//U Can Handel Boolean, Formula, Errors
//				}
//			}
//			System.out.println();
//		}
//	
//	}
//	
//	public static void writeXLSFile() throws IOException {
//		
//		String excelFileName = "C:/Test.xls";//name of excel file
//
//		String sheetName = "Sheet1";//name of sheet
//
//		HSSFWorkbook wb = new HSSFWorkbook();
//		HSSFSheet sheet = wb.createSheet(sheetName) ;
//
//		//iterating r number of rows
//		for (int r=0;r < 5; r++ )
//		{
//			HSSFRow row = sheet.createRow(r);
//	
//			//iterating c number of columns
//			for (int c=0;c < 5; c++ )
//			{
//				HSSFCell cell = row.createCell(c);
//				
//				cell.setCellValue("Cell "+r+" "+c);
//			}
//		}
//		
//		FileOutputStream fileOut = new FileOutputStream(excelFileName);
//		
//		//write this workbook to an Outputstream.
//		wb.write(fileOut);
//		fileOut.flush();
//		fileOut.close();
//	}
//	
//	public static void readXLSXFile() throws IOException
//	{
//		InputStream ExcelFileToRead = new FileInputStream("C:/Test.xlsx");
//		XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);
//		
//		XSSFWorkbook test = new XSSFWorkbook(); 
//		
//		XSSFSheet sheet = wb.getSheetAt(0);
//		XSSFRow row; 
//		XSSFCell cell;
//
//		Iterator rows = sheet.rowIterator();
//
//		while (rows.hasNext())
//		{
//			row=(XSSFRow) rows.next();
//			Iterator cells = row.cellIterator();
//			while (cells.hasNext())
//			{
//				cell=(XSSFCell) cells.next();
//		
//				if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
//				{
//					System.out.print(cell.getStringCellValue()+" ");
//				}
//				else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
//				{
//					System.out.print(cell.getNumericCellValue()+" ");
//				}
//				else
//				{
//					//U Can Handel Boolean, Formula, Errors
//				}
//			}
//			System.out.println();
//		}
//	
//	}
//	
//	public static void writeXLSXFile() throws IOException {
//		
//		String excelFileName = "C:/Test.xlsx";//name of excel file
//
//		String sheetName = "Sheet1";//name of sheet
//
//		XSSFWorkbook wb = new XSSFWorkbook();
//		XSSFSheet sheet = wb.createSheet(sheetName) ;
//
//		//iterating r number of rows
//		for (int r=0;r < 5; r++ )
//		{
//			XSSFRow row = sheet.createRow(r);
//
//			//iterating c number of columns
//			for (int c=0;c < 5; c++ )
//			{
//				XSSFCell cell = row.createCell(c);
//	
//				cell.setCellValue("Cell "+r+" "+c);
//			}
//		}
//
//		FileOutputStream fileOut = new FileOutputStream(excelFileName);
//
//		//write this workbook to an Outputstream.
//		wb.write(fileOut);
//		fileOut.flush();
//		fileOut.close();
//	}
//
//	public static void main(String[] args) throws IOException {
//		
//		writeXLSFile();
//		readXLSFile();
//		
//		writeXLSXFile();
//		readXLSXFile();
//
//	}
//
//}
