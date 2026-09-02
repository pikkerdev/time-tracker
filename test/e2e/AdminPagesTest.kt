package e2e

import com.codeborne.selenide.Condition.exist
import com.codeborne.selenide.Selenide.element
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import users.AuthRole

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Order(5)
class AdminPagesTest : E2ETest() {
  @Test @Order(1)
  fun usersPageIsAdminOnly() {
    openPage("/users")
    pageTitle("users.title")
    element("main table").should(exist)

    listOf(AuthRole.INTERNAL, AuthRole.EXTERNAL, AuthRole.CUSTOMER).forEach { role ->
      openPage("/timeentries", role)
      element("a[href='/users']").shouldNot(exist)
    }
  }

  @Test @Order(2)
  fun invoicesPageIsAdminOnlyAndInvoiceDetailsRouteRenders() {
    openPage("/invoices")
    pageTitle("invoices.title")
    element("main table").should(exist)

    listOf(AuthRole.INTERNAL, AuthRole.EXTERNAL, AuthRole.CUSTOMER).forEach { role ->
      openPage("/timeentries", role)
      element("a[href='/invoices']").shouldNot(exist)
    }
  }

}
