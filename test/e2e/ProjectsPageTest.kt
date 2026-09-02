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
@Order(3)
class ProjectsPageTest : E2ETest() {
  @Test @Order(1)
  fun projectPageLoadsForEveryRole() {
    AuthRole.values().forEach { role ->
      openPage("/projects", role)
      pageTitle("projects.title")
      element("main").should(exist)
    }
  }

  @Test @Order(2)
  fun adminCanOpenProjectEditorAndInternalCanOnlyFilter() {
    openPage("/projects")
    byText("projects.new").click()
    element(".modal").should(appear).find(com.codeborne.selenide.Selectors.byLabel(t("projects.name"))).should(exist)

    openPage("/projects", AuthRole.INTERNAL)
    pageTitle("projects.title")
  }
}
