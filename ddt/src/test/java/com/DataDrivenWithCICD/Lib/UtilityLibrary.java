package com.DataDrivenWithCICD.Lib;


import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

/***
 * This class will provide basic selenium functionalities using helper methods.
 * These methods should work in any environment and different browsers
 * 
 * @author Frank
 */
public class UtilityLibrary {

	final static Logger logger = Logger.getLogger(UtilityLibrary.class);
	private WebDriver driver;
	public boolean isDemoMode = false;
	public boolean isRemote = false;
	public List<String> errorScreenshots;

	public WebDriver startRemoteBrowser(String hubURL, String browserName) {
		DesiredCapabilities cap = new DesiredCapabilities();
		try {
			if (browserName.contains("chrome")) {
				cap.setBrowserName(browserName);

			} else if (browserName.contains("ie")) {
				cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				cap.setBrowserName("internet explorer");
			}
			// add Firefox, Safari browsers

			driver = new RemoteWebDriver(new URL(hubURL), cap);
		} catch (Exception e) {
			logger.error("starting remote browser failed for '" + browserName + "'", e);
		}
		setDefaultWaitConfiguration();
		isRemote = true;
		return driver;
	}

	/***
	 * This method used for any checkbox web-elements. If user want to check the
	 * box, pass isCheck=true else pass isCheck=false.
	 * 
	 * @param by
	 * @param isCheck
	 */
	public void selectCheckBox(By by, boolean isCheck) {
		WebElement checkBoxElem = driver.findElement(by);
		highlightElement(checkBoxElem);
		boolean checkboxState = checkBoxElem.isSelected();
		if (isCheck == true) // user wants to check the box
		{
			if (checkboxState == true) // by default checked
			{
				/* do nothing */} else // by default un-checked
			{
				checkBoxElem.click();
			}
		} else // user wants to un-check the box
		{
			if (checkboxState == true) {
				checkBoxElem.click();
			} else {
				/* do nothing */}
		}
	}

	/***
	 * This method starts browser for given type
	 * 
	 * @param browserType
	 * @return driver
	 */
	public WebDriver startLocalBrowser(String browserType) {
		if (browserType.contains("chrome")) {
			driver = startChromeBrowser();
		} else if (browserType.contains("firefox")) {
			driver = startFirefoxBrowser();
		} else if (browserType.contains("ie")) {
			driver = startIEBrowser();
		} else {
			logger.info("You chose: '" + browserType + "'");
			logger.info("Sorry, we curently support 'chrome, firefox, & ie' browsers.");
			logger.info("Please contact our Test Automation team.");
		}
		logger.info("starting browser: '" + browserType + "'");
		return driver;
	}

	/***
	 * This method starts Chrome browser
	 * 
	 * @return driver
	 */
	private WebDriver startChromeBrowser() {
		try {
			System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
			driver = new ChromeDriver();
			setDefaultWaitConfiguration();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return driver;
	}

	/***
	 * This method starts Firefox browser
	 * 
	 * @return driver
	 */
	private WebDriver startFirefoxBrowser() {
		try {
			System.setProperty("webdriver.gecko.driver", "src/test/resources/geckodriver.exe");
			driver = new FirefoxDriver();
			setDefaultWaitConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return driver;
	}

	/***
	 * This method starts IE browser
	 * 
	 * @return
	 */
	private WebDriver startIEBrowser() {
		try {
			System.setProperty("webdriver.ie.driver", "src/test/resources/IEDriverServer.exe");
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			driver = new InternetExplorerDriver(capabilities);
			setDefaultWaitConfiguration();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e);
		}
		return driver;
	}

	private void setDefaultWaitConfiguration() {
		try {
			driver.manage().window().maximize();
			driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}

	/***
	 * This method enters text to the text field web-element
	 * 
	 * @param by
	 * @param inputString
	 */
	public void enterTextField(By by, String inputString) {
		try {
			WebElement textFieldElem = driver.findElement(by);
			highlightElement(textFieldElem);
			textFieldElem.clear();
			textFieldElem.sendKeys(inputString);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e);
		}
	}

	/***
	 * This method selects dropdown web-elements
	 * 
	 * @param by
	 * @param optionValue
	 */
	public void selectDropDown(By by, String optionValue) {
		try {
			WebElement dropdownElem = driver.findElement(by);
			highlightElement(dropdownElem);
			Select dropdownSelect = new Select(dropdownElem);
			dropdownSelect.selectByVisibleText(optionValue);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e);
		}
	}

	/***
	 * This method clicks on button
	 * 
	 * @param by
	 */
	public void clickButton(By by) {
		try {
			WebElement btnElem = driver.findElement(by);
			highlightElement(btnElem);
			btnElem.click();
		} catch (Exception e) {
			logger.error(e);
			// e.printStackTrace();
		}
	}

	public void clickButton(WebElement element) {
		try {
			highlightElement(element);
			element.click();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e);
		}

	}

	/***
	 * This is customWait method using Thread.sleep
	 * 
	 * @param inSeconds
	 */
	public void customWait(int inSeconds) {
		try {
			Thread.sleep(inSeconds * 1000);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/***
	 * This is a dynamic wait. User can use it when switch browser windows. When
	 * web-page synchronization happens.
	 * 
	 * @param by
	 * @return
	 */
	public WebElement fluentWait(final By by) {
		WebElement targetElem = null;
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(60, TimeUnit.SECONDS)
					.pollingEvery(3, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);

			targetElem = wait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver driver) {

					return driver.findElement(by);
				}
			});
		} catch (Exception e) {
			logger.error(e);
		}
		highlightElement(targetElem);
		return targetElem;
	}

	/***
	 * This method captures the screenshot of the browser
	 * 
	 * @param screenshotFileName
	 * @param filePathToSave
	 * @return full path of the image name and location
	 */
	public String captureScreenshot(String screenshotFileName, String filePathToSave) {
		String finalImage = null;
		try {
			if (isRemote == true) {
				driver = new Augmenter().augment(driver);
			}
			String tempName = filePathToSave + screenshotFileName + getCurrentTime() + ".png";
			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(srcFile, new File(tempName));
			finalImage = tempName;
		} catch (Exception e) {
			logger.error(e);
		}
		return finalImage;
	}

	/***
	 * This method calculates current time
	 * 
	 * @return returns the value of current time
	 */
	public String getCurrentTime() {
		String finalTimeStamp = null;
		try {
			Date date = new Date();
			String tempTime = new Timestamp(date.getTime()).toString();
			finalTimeStamp = tempTime.replace(":", "_").replace("-", "_").replace(".", "_").replace(" ", "_");
			logger.debug("time: " + finalTimeStamp);
		} catch (Exception e) {
			logger.error(e);
		}
		return finalTimeStamp;
	}

	/***
	 * This method highlights web-elements using javascript executor
	 * 
	 * @param by
	 */
	public void highlightElement(By by) {
		if (isDemoMode == true) {
			try {
				WebElement temp = driver.findElement(by);
				WrapsDriver wrappedElement = (WrapsDriver) temp;
				JavascriptExecutor js = (JavascriptExecutor) wrappedElement.getWrappedDriver();
				customWait(1);
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);", temp,
						"color: red; border: 2px solid yellow;");
				customWait(1);
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);", temp, "");

			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	/***
	 * This method highlights web-elements using javascript executor
	 * 
	 * @param element
	 */
	public void highlightElement(WebElement element) {
		if (isDemoMode == true) {
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
				logger.error(e);
			}
		}
	}

	/***
	 * This method switch to iframe content
	 * 
	 * @param by
	 */
	public void switchToIframe(By by) {
		try {
			WebElement iframe = driver.findElement(by);
			driver.switchTo().frame(iframe);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/***
	 * This method switch to iframe content
	 * 
	 * @param element
	 */
	public void switchToIframe(WebElement element) {
		try {

			driver.switchTo().frame(element);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/***
	 * This method switch to default content from iframe content
	 */
	public void switchToDefault() {
		try {
			driver.switchTo().defaultContent();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/***
	 * This method move mouse pointer to the given WebElement
	 * 
	 * @param toElement
	 */
	public void moveMouseToElement(WebElement toElement) {
		try {
			Actions action = new Actions(driver);
			action.moveToElement(toElement).build().perform();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/***
	 * This method move mouse pointer to first element then to second element
	 * 
	 * @param fistElem
	 * @param secondElem
	 */
	public void moveMouseToElement(WebElement fistElem, WebElement secondElem) {
		try {
			Actions action = new Actions(driver);
			action.moveToElement(fistElem).build().perform();
			customWait(2);
			action.moveToElement(secondElem).build().perform();
			customWait(2);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/***
	 * This method clicks located link
	 * 
	 * @param by
	 */
	public void clickLink(By by) {
		try {
			WebElement link = driver.findElement(by);
			highlightElement(link);
			link.click();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * This method finds page title
	 * 
	 * @return page title string
	 */
	public String verifyPageTitle() {
		String pageTitle = null;
		try {
			pageTitle = driver.getTitle();
			logger.info("Website title: " + pageTitle);
		} catch (Exception e) {
			logger.error(e);
		}
		return pageTitle;
	}

	/***
	 * This method clicks on hidden element
	 * 
	 * @param by
	 */
	public void clickOnHiddenElement(By by) {

		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", driver.findElement(by));
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/***
	 * This method clicks on hidden element
	 * 
	 * @param element
	 */
	public void clickOnHiddenElement(WebElement element) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", element);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public WebElement explicitWaitForCondition(By by) {
		WebElement elem = null;
		WebElement myDynamicElement = (new WebDriverWait(driver, 10))
				.until(ExpectedConditions.visibilityOf(driver.findElement(by)));
		elem = myDynamicElement;
		return elem;
	}

	public List<String> automaticallyAttachErrorImgToEmail() throws Exception {
		// System.out.println("research this method and try to complete before
		// next class.!!!");
		List<String> fileNames = new ArrayList<>();
		JavaPropertiesManager propertyReader = new JavaPropertiesManager("src/test/resources/dynamicConfig.properties");

		String tempTimeStamp = propertyReader.readProperty("sessionTime");
		String numberTimeStamp = tempTimeStamp.replaceAll("_", "");
		long testStartTime = Long.parseLong(numberTimeStamp);

		// first check if error-screenshot folder has file
		File file = new File("target/images");
		if (file.isDirectory()) {
			if (file.list().length > 0) {
				File[] screenshotFiles = file.listFiles();
				for (int i = 0; i < screenshotFiles.length; i++) {
					// checking if file is file, not a folder
					if (screenshotFiles[i].isFile()) {
						String eachFileName = screenshotFiles[i].getName();
						int indexOf20 = searchStringInString("20", eachFileName);
						String timeStampFromScreenshotFile = eachFileName.substring(indexOf20,
								eachFileName.length() - 4);
						// System.out.println("Screenshot File Name: " +
						// timeStampFromScreenshotFile);
						String fileNumberStamp = timeStampFromScreenshotFile.replace("_", "");
						long numbers = Long.parseLong(fileNumberStamp);
						// System.out.println("bfore numbers: " + numbers);

						numbers = Long.parseLong(fileNumberStamp.substring(0, 14));
						testStartTime = Long.parseLong(numberTimeStamp.substring(0, 14));

						// System.out.println("numbers: " + numbers);
						// System.out.println("teststart time: " +
						// testStartTime);
						// Only add the screenshots taken for the last running
						// tests
						if (numbers > testStartTime) {
							fileNames.add("target/images/" + eachFileName);
						}
					}
				}
			}
		}
		errorScreenshots = fileNames;
		return fileNames;
	}

	/***
	 * This method searches the target sub-string in given string message
	 * 
	 * @param target
	 * @param message
	 * @return index of first target sub-string match
	 */
	private int searchStringInString(String target, String message) {
		int targetIndex = 0;
		for (int i = -1; (i = message.indexOf(target, i + 1)) != -1;) {
			targetIndex = i;
			break;
		}

		return targetIndex;
	}

	public void fileUpload(By by, String filePath) {
		try {
			WebElement fileUpload = driver.findElement(by);
			File file = new File(filePath);
			String fullPath = file.getAbsolutePath();
			if (isRemote == true) {
				((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			}
			fileUpload.sendKeys(fullPath);
		} catch (Exception e) {
			logger.error("Remmote file upload failed: ", e);
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
