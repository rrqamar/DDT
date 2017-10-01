package com.DataDrivenWithCICD.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Parking {
	private WebDriver driver;
	
	@DataProvider(name="ParkingTest")
	public Object[][] dataFromUser() throws Exception
    {
        Object[][] data = testData("src/test/resources/parking.xls", "test2");
        return data;
    }
     
    
	private Object[][] testData(String fileName, String sheetName) {
		String[][] arrayExcelData = null;
		try {
			FileInputStream fs = new FileInputStream(fileName);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(sheetName);

			int totalNoOfCols = sh.getColumns();
			int totalNoOfRows = sh.getRows();
			
			arrayExcelData = new String[totalNoOfRows-1][totalNoOfCols];
			
			for (int i= 1 ; i < totalNoOfRows; i++) {

				for (int j=0; j < totalNoOfCols; j++) {
					arrayExcelData[i-1][j] = sh.getCell(j, i).getContents();
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		}
		//System.out.println(arrayExcelData[0][1]);
		return arrayExcelData;
	}
	
	
	
	@Test(dataProvider = "ParkingTest")
	  public void parkingCalac(String term, String inT, String amPm, String inDate,String outT,
			  String AmPm, String outDate) {
		  driver.get("http://adam.goucher.ca/parkcalc/");
		  customWait(2);

			/// Test case#1 Short-Term Parking AM rate calculating
			selectTerm(term);
			inTimeDate(inT, amPm, inDate);
			outTimeDate(outT, AmPm, outDate);
			calcBtn();
			customWait(1);
			System.out.println(result());
	  }
	  @BeforeMethod
	  public void beforeMethod() {
		  System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
	  }

	  @AfterMethod
	  public void afterMethod() throws Exception {
			Thread.sleep(3*1000);
			driver.close();
			driver.quit();
			
		}

	  
	  
/////////////////////////////// Helper///////////////////
/***
* This method will select parking Term
* @param parkingTerm
*/
public void selectTerm(String parkingTerm)
{
WebElement shortTermAM = driver.findElement(By.id("Lot"));
shortTermAM.click();
customWait(1);
Select selectTerm = new Select(shortTermAM);
selectTerm.selectByVisibleText(parkingTerm);
customWait(1);
}


/***
* this method enter Enter time
* @param time
* @param amPm
* @param date
*/
public void inTimeDate(String time,String amPm, String date)
{
WebElement inTime=driver.findElement(By.id("EntryTime"));
inTime.clear();
inTime.sendKeys(time);
if(amPm=="am" || amPm== "AM")
{
WebElement amPmBtn=driver.findElement(By.cssSelector("input[value='AM']"));
amPmBtn.click();
}else
{
WebElement amPmBtn=driver.findElement(By.cssSelector("input[value='PM']"));
amPmBtn.click();
}
WebElement inDate=driver.findElement(By.id("EntryDate"));
inDate.clear();
inDate.click();
inDate.sendKeys(date);

}
/***
* This method enter Exit time and date
* @param time
* @param amPM
* @param date
*/
public void outTimeDate(String time,String amPM,String date)
{
WebElement outTime = driver.findElement(By.id("ExitTime"));
outTime.clear();
outTime.sendKeys(time);
if(amPM=="am" || amPM=="AM"){
WebElement ampmBtn = driver.findElement(By.xpath("html/body/form/table/tbody/tr[3]/td[2]/font/input[2]"));
ampmBtn.click();
}else{
WebElement ampmBtn = driver.findElement(By.xpath("html/body/form/table/tbody/tr[3]/td[2]/font/input[3]"));
ampmBtn.click();
}
WebElement outDate = driver.findElement(By.id("ExitDate"));
outDate.clear();
outDate.click();
outDate.sendKeys(date);	

}
/***
* click calculate button and highlight result
*/
public void calcBtn()
{
WebElement calcBtn = driver.findElement(By.cssSelector("input[value='Calculate']"));
calcBtn.click();
WebElement result=driver.findElement(By.xpath("html/body/form/table/tbody/tr[4]/td[2]"));
highlightElement(result);
}

public void highlightElement(WebElement element) {
try {
for (int i = 0; i < 4; i++) {
WrapsDriver wrappedElement = (WrapsDriver) element;
JavascriptExecutor js = (JavascriptExecutor) wrappedElement.getWrappedDriver();
Thread.sleep(500);
js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element,
"color: red; border: 2px solid yellow;");
Thread.sleep(500);
js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "");
}
} catch (Exception e) {

}
}

public String result()
{
WebElement resultElm=driver.findElement(By.cssSelector(".SubHead>font>b"));
String result=resultElm.getText();
return result;
}

public void customWait(int n) {
try {
Thread.sleep(n * 1000);
} catch (InterruptedException e) {
e.printStackTrace();
}

}
}


