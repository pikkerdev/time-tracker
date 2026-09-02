package e2e

import com.codeborne.selenide.Condition.exist
import com.codeborne.selenide.Condition.text
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.element
import com.codeborne.selenide.WebDriverRunner
import com.codeborne.selenide.junit5.TextReportExtension
import klite.Config
import klite.i18n.Lang
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import users.AuthRole
import main as launchApplication

@ExtendWith(TextReportExtension::class)
abstract class E2ETest {
  companion object {
    const val baseUrl = "http://localhost:18080"
    private var started = false

    @JvmStatic
    @BeforeAll
    fun startApplication() {
      if (started) return
      started = true
      Config.useEnvFile()
      Config["ENV"] = "test,dev,e2e"
      val databaseUrl = Config["DB_URL"]
      val databaseName = databaseUrl.substringAfterLast('/') + "_e2e"
      runCatching {
        java.sql.DriverManager.getConnection(
          databaseUrl.substringBeforeLast('/') + "/postgres",
          Config["DB_USER"],
          Config["DB_PASS"]
        ).use { connection ->
          connection.createStatement().execute("create database $databaseName")
        }
      }.onFailure { error ->
        check(error.message?.contains("already exists") == true) { error }
      }
      Config["DB_URL"] = "${databaseUrl}_e2e"
      Config["PORT"] = "18080"
      Config["E2E_AUTH"] = "true"
      Configuration.baseUrl = baseUrl
      Configuration.browserSize = "1366x900"
      Configuration.timeout = 5000
      Configuration.headless = true
      launchApplication()
      repeat(30) {
        runCatching { java.net.URI(baseUrl).toURL().openConnection().apply { connectTimeout = 500 }.connect() }.onSuccess { return }
        Thread.sleep(250)
      }
    }
  }

  protected fun t(key: String) = Lang.translate("en", key)

  protected fun login(role: AuthRole) {
    Selenide.closeWebDriver()
    Selenide.open("/api/e2e/login/${role.name.lowercase()}")
  }

  protected fun openPage(path: String, role: AuthRole = AuthRole.ADMIN) {
    login(role)
    Selenide.open(path)
    waitForApiRequestsToFinish()
  }

  protected fun pageTitle(key: String) = element("main h1").shouldHave(text(t(key)))

  protected fun waitForApiRequestsToFinish() {
    element(".loading, .spinner").shouldNot(exist)
  }

  protected fun byText(key: String) = element(com.codeborne.selenide.Selectors.byText(t(key)))

  protected fun currentPath() = (WebDriverRunner.url() ?: "").substringAfter(baseUrl)

  @AfterEach
  fun ensureNoFrontendErrors() {
    element("body").shouldNotHave(text("undefined"))
    element(".validated :invalid").shouldNot(exist)
    waitForApiRequestsToFinish()
    element(".toast .text-danger-400").shouldNot(exist)
  }
}
