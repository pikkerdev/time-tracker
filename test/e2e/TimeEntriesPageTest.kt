package e2e

import com.codeborne.selenide.CollectionCondition.size
import com.codeborne.selenide.Condition.exist
import com.codeborne.selenide.Selenide.element
import com.codeborne.selenide.Selenide.elements
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import users.AuthRole

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Order(4)
class TimeEntriesPageTest : E2ETest() {
  @Test @Order(1)
  fun timeEntriesListAndFiltersAreAvailableToEveryRole() {
    AuthRole.values().forEach { role ->
      openPage("/timeentries", role)
      pageTitle("timeEntries.title")
      elements("input[type='date']").shouldHave(size(2))
      elements("select").shouldHave(size(2))
    }
  }

  @Test @Order(2)
  fun adminOnlyBulkActionsAndMyEntriesFilter() {
    openPage("/timeentries")
    byText("timeEntries.showMyTimeEntries").should(exist)
    byText("timeEntries.editEntries").shouldNot(exist)

    openPage("/timeentries", AuthRole.INTERNAL)
    byText("timeEntries.showMyTimeEntries").shouldNot(exist)
    byText("timeEntries.editEntries").shouldNot(exist)
  }

  @Test @Order(3)
  fun internalAndExternalCanOpenEntryEditor() {
    listOf(AuthRole.INTERNAL, AuthRole.EXTERNAL).forEach { role ->
      openPage("/entry", role)
      element("form").should(exist)
      byText("timeEntries.hours").should(exist)
    }
  }
}
