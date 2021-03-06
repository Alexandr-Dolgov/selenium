import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

import java.text.SimpleDateFormat

class GitHubTest {

    private WebDriver driver
    private String login
    private String password
    private String repositoryName = ''

    GitHubTest(WebDriver driver, String login, String password) {
        this.driver = driver
        this.login = login
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
                return d.title.startsWith(login)
            }
        })
    }


    void checkUserName() {
        assert driver.findElement(By.cssSelector("div.vcard-username")).text == login
    }

    void checkFullName(String expectedFullName) {
        assert driver.findElement(By.cssSelector("div.vcard-fullname")).text == expectedFullName
    }

    void openRepositoriesPage() {
        driver.findElement(By.cssSelector("a[href*='tab=repositories']")).click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.findElements(By.cssSelector('div.repo-tab')).size() == 1
            }
        })
    }

    void openRepository(String repositoryName) {
        this.repositoryName = repositoryName

        driver.findElement(By.cssSelector("a[href='/$login/$repositoryName']")).click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.title == "$login/$repositoryName".toString()
            }
        })
    }

    void openIssuesPage() {
        driver.findElement(By.cssSelector("a[href^='/$login'][href\$='/issues']")).click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.title == "Issues · $login/$repositoryName".toString()
            }
        })
    }

    List<Issue> getAllOpenIssues() {
//        login = 'grails'
//        repositoryName = 'grails-core'
//        driver.get("https://github.com/$login/$repositoryName/issues")
//        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
//            Boolean apply(WebDriver d) {
//                return d.title == "Issues · $login/$repositoryName".toString()
//            }
//        })

        List<Issue> res = openIssuesInCurrentPage

        WebElement el
        while (el = driver.findElements(By.cssSelector("a.next_page"))[0]) {
            int nextPageNum = driver.findElement(By.cssSelector("em.current")).text.toInteger() + 1
            el.click()

            (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
                Boolean apply(WebDriver d) {
                    return d.findElement(By.cssSelector("em.current")).text.toInteger() == nextPageNum
                }
            })

            res += this.openIssuesInCurrentPage
        }

        //проверяем что количество найденных issues соостветствует отображаемому количеству issues
        //здесь может быть проблема если новая issue создастся после завершения поиска, но до assert
        assert res.size() == driver.findElement(By.cssSelector("a[href^='/$login'][href\$='/issues']"))
                .findElement(By.cssSelector("span.counter")).text.toInteger()

        return res
    }

    List<Issue> getOpenIssuesInCurrentPage() {
        return driver.findElements(By.cssSelector("a.issue-title-link")).collect { element ->
            def issue = new Issue()
            issue.url = element.getAttribute('href')
            issue.title = element.text
            return issue
        }
    }

    void closeAllIssues(List<Issue> issues) {
        if (issues.size() == 0) {
            println('нет ни одного открытого issue')
            return
        }

        issues.each { Issue issue ->
            String issueLink = issue.url
            driver.get(issueLink)
            int issuesId = issueLink.split('/').last().toInteger()

            (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
                Boolean apply(WebDriver d) {
                    return d.title.endsWith("Issue #$issuesId · $login/$repositoryName")
                }
            })

            driver.findElement(By.cssSelector("button[name='comment_and_close']")).click()

            (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
                Boolean apply(WebDriver d) {
                    if (d.findElements(By.cssSelector("div.state-closed")).size() == 1) {
                        return true
                    } else {
                        d.navigate().refresh()
                        return false
                    }
                }
            })
        }

        openIssuesPage()
    }

    void createNewIssue(String issueName) {
        driver.findElement(By.cssSelector("a.btn.btn-primary[href='/$login/$repositoryName/issues/new']")).click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.title == "New Issue · $login/$repositoryName".toString()
            }
        })

        driver.findElement(By.cssSelector("input[name='issue[title]']")).sendKeys(issueName + Keys.ENTER)

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.title.startsWith("$issueName · Issue")
            }
        })

        openIssuesPage()
    }

    void commentIssue(List<Issue> issues, String issueName, String comment) {
        Issue issue = issues.find { it.title == issueName}
        if (issue == null) {
            println("issue с именем $issueName не найдено")
        }

        String issueLink = issue.url
        driver.get(issueLink)

        //убедиться что нужная страница открылась
        int issuesId = issueLink.split('/').last().toInteger()
        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.title.endsWith("Issue #$issuesId · $login/$repositoryName")
            }
        })

        WebElement element = driver.findElement(By.cssSelector("textarea[name='comment[body]']"))
        element.sendKeys(comment + Keys.CONTROL + Keys.ENTER)
    }

    void signOut() {
        driver.findElement(By.cssSelector("ul#user-links.header-nav.user-nav.right"))
                .findElement(By.cssSelector("a.header-nav-link.tooltipped[aria-label='View profile and more']"))
                .click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.findElements(By.cssSelector("form.logout-form")).size() == 1
            }
        })

        driver.findElement(By.cssSelector("form.logout-form")).click()

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            Boolean apply(WebDriver d) {
                return d.currentUrl == 'https://github.com/'
            }
        })
    }

}