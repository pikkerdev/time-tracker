package e2e

import com.codeborne.selenide.Condition.exist
import com.codeborne.selenide.Condition.text
import com.codeborne.selenide.Selenide.element
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import users.AuthRole

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Order(1)
class HomeAndNavigationTest : E2ETest() {
  @Test @Order(1)
  fun anonymousHomeShowsLogin() {
    com.codeborne.selenide.Selenide.closeWebDriver()
    com.codeborne.selenide.Selenide.open("/")
    element("body").shouldHave(text(t("home.intro")), text(t("login.googleLogin")))
  }

  @Test @Order(2)
  fun eachRoleGetsItsExpectedLandingPage() {
    AuthRole.values().forEach { role ->
      openPage("/", role)
      if (role == AuthRole.CUSTOMER) {
        pageTitle("timeEntries.title")
        element("a[href='/customers']").shouldNot(exist)
      } else {
        element("main").should(exist)
      }
    }
  }

  @Test @Order(3)
  fun navigationIsScopedByRole() {
    openPage("/entry", AuthRole.ADMIN)
    element("a[href='/users']").should(exist)
    element("a[href='/invoices']").should(exist)

    openPage("/entry", AuthRole.INTERNAL)
    element("a[href='/users']").shouldNot(exist)
    element("a[href='/invoices']").shouldNot(exist)

    openPage("/timeentries", AuthRole.EXTERNAL)
    element("a[href='/customers']").shouldNot(exist)
    element("a[href='/projects']").should(exist)

    openPage("/timeentries", AuthRole.CUSTOMER)
    element("a[href='/projects']").should(exist)
    element("a[href='/customers']").shouldNot(exist)
  }
}
