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
        //8. Закрыть все по очереди
        //9. Создать 5 новых
        //10. Найти все открытые
        //11. Выбрать один по имени и прокомментировать его
        //12. Выйти


        driver.quit()
    }

}