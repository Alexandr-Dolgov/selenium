import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

class App {

    static void main(String[] args) {
        System.setProperty('webdriver.chrome.driver', 'chromedriver')
        WebDriver driver = new ChromeDriver()

        GitHubTest github = new GitHubTest(driver, 'login', 'password')

        github.signIn()
        github.openProfile()
        github.checkUserName()
        github.checkFullName('Full Name')

        driver.quit()
    }

}