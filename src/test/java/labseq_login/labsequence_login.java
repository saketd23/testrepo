package labseq_login;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.github.bonigarcia.wdm.WebDriverManager;

public class labsequence_login {
	public static WebDriver driver;

	/**
	 * To setup ChromeDriver as the standalone server that implements WebDriver
	 * standards. ChromeDriver will allow automated testing of the web application.
	 */
	@BeforeClass
	public void setup() {
		WebDriverManager.chromedriver().driverVersion("88.0.4324.27").setup();
                DesiredCapabilities capability = new DesiredCapabilities();
                capability.setCapability("binary", "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
                driver = new ChromeDriver(capability);
	}

	/**
	 * Opens Mock Idp. Enters credentials, and login button gets clicked. From the
	 * dropdown, selects the section and navigation to the section takes place.
	 * Scrolls to the bottom of the page and logs out.
	 * 
	 * @throws IOException
	 */
	@Test
	public void login_labseq() throws IOException {
		// Read login_data.properties
		FileReader fileReader = null;
		try {
			fileReader = new FileReader("login_data.properties");
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
		Properties properties = new Properties();
		properties.load(fileReader);

		// Navigate to url
		try {
			driver.get(properties.getProperty("url"));
		} catch (UnreachableBrowserException ex) {
			System.out.println(ex.getMessage());
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {
			// Enter Credentials
			driver.findElement(By.xpath("//*[@id=\"principal\"]")).sendKeys(properties.getProperty("Principal"));
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// Click Submit Button - Login Complete
			driver.findElement(By.xpath("//*[@id=\"sessionsData\"]/article/div[3]/div[2]/button")).click();
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			WebElement section_dropdown = driver.findElement(By.xpath("//*[@id=\"change_section_section\"]"));
			Select select_section = new Select(section_dropdown);

			// Select section from dropdown
			select_section.selectByVisibleText(properties.getProperty("section"));
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			driver.findElement(By.xpath("//*[@id=\"tasks\"]")).click();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

			String current_section = driver.findElement(By.xpath("//*[@id=\"global-wrapper\"]/header/ul/li[1]/span"))
					.getText();
			Assert.assertEquals(current_section, "LabSeq Admin at Extraction", "Wrong Section");

			// Scroll to the bottom
			WebElement bottom_element = driver
					.findElement(By.xpath("//*[@id=\"global-wrapper\"]/div[1]/div/footer[1]/small/a"));
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].scrollIntoView();", bottom_element);

			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			// Logout from Lab Sequence
			String logout_url = driver.findElement(By.xpath("//*[@id=\"logoutLink\"]")).getAttribute("href");
			driver.navigate().to(logout_url);
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		} catch (NoSuchElementException ex) {
			System.out.println(ex.getMessage());
		} catch (TimeoutException ex) {
			System.out.println(ex.getMessage());
		} catch (ElementNotInteractableException ex) {
			System.out.println(ex.getMessage());
		}

	}

	/**
	 * Closes the driver once the workflow is complete.
	 */
	@AfterClass
	public void close_labseq() {
		driver.close();
	}

}
