package com.tongji;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class testAll {
    private static WebDriver driver;
    private String projectName;

    @BeforeClass
    public void beforeClass() throws InterruptedException {
        String localChromeDriver = "C://Program Files//Mozilla Firefox//firefox.exe";
        System.setProperty("webdriver.firefox.bin", localChromeDriver);
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test()
    public void testAll() throws Exception {
        String url = "http://localhost/testlink/login.php";
        driver.get(url);
        Assert.assertTrue(driver.getTitle().indexOf("Login") > -1);
    }

    @Test(dependsOnMethods = "testAll")
    public void testCreateProject() throws InterruptedException {
        Thread.sleep(2000);
        driver.findElement(By.id("tl_login")).clear();
        driver.findElement(By.id("tl_login")).sendKeys("admin");
        driver.findElement(By.name("tl_password")).clear();
        driver.findElement(By.name("tl_password")).sendKeys("admin");
        driver.findElement(By.xpath("//input[@value='Log in']")).click();
        Thread.sleep(2000);

        String projectName = "4664645";
        driver.findElement(By.name("tprojectName")).click();
        driver.findElement(By.name("tprojectName")).sendKeys(projectName);
        driver.findElement(By.name("tcasePrefix")).click();
        driver.findElement(By.name("tcasePrefix")).sendKeys(projectName);
        driver.findElement(By.name("doActionButton")).click();
//        driver.findElement(By.name("toActionButton")).click();
    }

    @Test(dependsOnMethods = {"testAll", "testCreateProject"})
    public void testDeleteProject() throws InterruptedException {
        driver.get("http://127.0.0.1/testlink/lib/project/projectView.php");

        Thread.sleep(1000);
        String xpathDeleteButton = "//tr/td[1]/a[contains(text(), '" + projectName +"')]/../following-sibling::td[8]/img";
        driver.findElement(By.xpath(xpathDeleteButton)).click();
        Thread.sleep(1000);
        driver.findElement(By.id("ext-gen20")).click();
    }

    @Test
    public void login() throws InterruptedException {
        String url = "http://127.0.0.1/testlink/login.php";
        driver.get(url);

        Thread.sleep(2000);

        if (driver.getPageSource().indexOf("Logout") > -1) driver.findElement(By.linkText("Logout")).click();

        Thread.sleep(2000);
        driver.findElement(By.id("tl_login")).clear();
        driver.findElement(By.id("tl_login")).sendKeys("admin");
        driver.findElement(By.name("tl_password")).clear();
        driver.findElement(By.name("tl_password")).sendKeys("admin");
        driver.findElement(By.xpath("//input[@value='Log in']")).click();

        Thread.sleep(2000);

        for (int second=0;;second++){
            if (second >= 60) throw new Error();
            try {
                if (driver.getCurrentUrl().indexOf("caller=login") > -1) break;
                if (driver.getPageSource().indexOf("Logout") > -1) driver.findElement(By.linkText("Logout")).click();
                else {
                    driver.findElement(By.id("tl_login")).clear();
                    driver.findElement(By.id("tl_login")).sendKeys("admin");
                    driver.findElement(By.name("tl_password")).clear();
                    driver.findElement(By.name("tl_password")).sendKeys("admin");
                    driver.findElement(By.xpath("//input[@value='Log in']")).click();

                    Thread.sleep(2000);
                }
            } catch (Exception e) {
            }
            Thread.sleep(2000);
        }
    }

    @DataProvider(name = "mailData")
    public static Object[][] mailRightAndWrong() {
        return new Object[][] {{"11111", false}, {"ABC" + (new Random().nextInt(10000)) + "@hello.com", true}};
    }

    @Test(dependsOnMethods = {"login",}, dataProvider = "mailData")
    public void testCreateUser(String mail, boolean isMailOK) throws InterruptedException {
        driver.navigate().to("http://127.0.0.1/testlink/index.php?caller=login");
        Thread.sleep(1000);

        driver.switchTo().defaultContent().switchTo().frame(0);
        driver.findElement(By.xpath("//div[3]/a[3]/img")).click();

        Thread.sleep(1000);
        driver.switchTo().defaultContent().switchTo().frame(1);
        driver.findElement(By.name("doCreate")).click();
        Thread.sleep(1000);

        driver.findElement(By.name("login")).clear();
        driver.findElement(By.name("login")).sendKeys(mail);
        driver.findElement(By.name("firstName")).clear();
        driver.findElement(By.name("firstName")).sendKeys("111");
        driver.findElement(By.name("lastName")).clear();
        driver.findElement(By.name("lastName")).sendKeys("111");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("Admin111");
        driver.findElement(By.id("email")).clear();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.name("do_update")).click();
        Thread.sleep(2000);

        if(!isMailOK) {
            for (int second=0;;second++) {
                if (second >= 60) throw new Error();
                try {
                    if ("OK".equals(driver.findElement(By.cssSelector("td.x-btn-mc")).getText())) break;
                } catch (Exception e) {
                }
                Thread.sleep(0);
            }

            driver.findElement(By.cssSelector("td.x-btn-mc")).click();
        } else {
            Assert.assertTrue(driver.findElements(By.xpath("//tr/td/div[contains(text(), '" + mail +"')]")).size() != 0);
        }

    }

    @BeforeSuite
    @Parameters({"testEnv"})
    public void beforeSuite(@Optional("insideTestEnv") String testEnv) throws InterruptedException, IOException {
        System.out.println("对应测试环境:" + testEnv);
    }
}