import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

class GitHubTest {

    private WebDriver driver
    private String login    //login in lower case
    private String password

    GitHubTest(WebDriver driver, String login, String password) {
        this.driver = driver
        this.login = login.toLowerCase()
        this.password = password
    }

    void signIn() {
        driver.get('https://github.com/')

        driver.findElement(By.linkText('Sign in')).click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.title.startsWith('Sign in to GitHub')
            }
        })

        driver.findElement(By.name('login')).sendKeys(login)
        driver.findElement(By.name('password')).sendKeys(password)
        driver.findElement(By.name('commit')).click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.findElements(By.className('header-logged-in')).size() == 1
            }
        })
    }

    void openProfile() {
        //http://ddavison.io/css/2014/02/18/effective-css-selectors.html
        driver.findElement(By.cssSelector("ul#user-links.header-nav.user-nav.right"))
                .findElement(By.cssSelector("a.header-nav-link.tooltipped[aria-label='View profile and more']"))
                .click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.findElements(By.linkText('Your profile')).size() == 1
            }
        })

        driver.findElement(By.linkText('Your profile')).click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.title.toLowerCase().startsWith(login)
            }
        })
    }


    void checkUserName() {
        assert driver.findElement(By.cssSelector("div.vcard-username")).text.toLowerCase() == login
    }

    void checkFullName(String expectedFullName) {
        assert driver.findElement(By.cssSelector("div.vcard-fullname")).text == expectedFullName
    }

}