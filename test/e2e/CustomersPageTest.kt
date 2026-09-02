package e2e

import com.codeborne.selenide.Condition.appear
import com.codeborne.selenide.Condition.exist
import com.codeborne.selenide.Selenide.element
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import users.AuthRole

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Order(2)
class CustomersPageTest : E2ETest() {
  @Test @Order(1)
  fun adminCanOpenCustomerEditor() {
    openPage("/customers")
    pageTitle("customers.title")
    byText("customers.add").click()
    element(".modal").should(appear).find(com.codeborne.selenide.Selectors.byLabel(t("customers.name"))).should(exist)
  }

  @Test @Order(2)
  fun internalCanViewCustomersButCannotCreateThem() {
    openPage("/customers", AuthRole.INTERNAL)
    pageTitle("customers.title")
    element("main table").should(exist)
  }

  @Test @Order(3)
  fun externalAndCustomerDoNotSeeCustomerNavigation() {
    listOf(AuthRole.EXTERNAL, AuthRole.CUSTOMER).forEach { role ->
      openPage("/timeentries", role)
      element("a[href='/customers']").shouldNot(exist)
    }
  }
}
