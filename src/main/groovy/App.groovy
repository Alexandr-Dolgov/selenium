import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

class App {

    static void main(String[] args) {
        System.setProperty('webdriver.chrome.driver', 'chromedriver')
        WebDriver driver = new ChromeDriver()

        GitHubTest github = new GitHubTest(driver, 'Alexandr-Dolgov', 'password')

        //1. Войти
        github.signIn()

        //2. Перейти на страницу профиля
        github.openProfile()

        //3. Проверить имя
        github.checkUserName()
        github.checkFullName('Alexandr Dolgov')

        //4. Перейти на страницу репозиториев
        github.openRepositoriesPage()

        //5. Найти репозиторий по имени и открыть его
        github.openRepository('selenium')

        //6. Перейти на страницу Issues
        github.openIssuesPage()

        //7. Найти все открытые
        List<Issue> allOpenIssues = github.allOpenIssues

        //8. Закрыть все по очереди
        github.closeAllIssues(allOpenIssues)

        //9. Создать 5 новых
        5.times { github.createNewIssue(it.toString()) }

        //10. Найти все открытые
        allOpenIssues = github.allOpenIssues

        //11. Выбрать один по имени и прокомментировать его
        String issueName = '2'
        String comment = 'hello'
        github.commentIssue(allOpenIssues, issueName, comment)

        //12. Выйти
        github.signOut()

        driver.quit()
    }

}