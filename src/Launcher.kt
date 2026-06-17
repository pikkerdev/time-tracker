import auth.AccessChecker
import auth.AuthUserProvider
import auth.Public
import customers.CustomerRoutes
import db.initDB
import invoices.InvoiceRoutes
import klite.*
import klite.annotations.annotated
import klite.http.httpClient
import klite.jdbc.RequestTransactionHandler
import klite.json.JsonBody
import klite.oauth.AuthRoutes
import klite.oauth.GoogleOAuthClient
import klite.oauth.OAuthRoutes
import klite.oauth.OAuthUserProvider
import projects.ProjectRoutes
import users.UserRoutes
import java.nio.file.Path
import kotlin.reflect.full.primaryConstructor
import kotlin.time.Duration.Companion.days

fun main() {
  if (!Config.isProd) Config.useEnvFile()

  Server(
    sessionStore = CookieSessionStore(cookie = Cookie("S", "", httpOnly = true, secure = Config.isProd, maxAge = 365.days)),
    httpExchangeCreator = XForwardedHttpExchange::class.primaryConstructor!!
  ).apply {
    initDB()

    register(httpClient())

    use<JsonBody>()
    use<RequestTransactionHandler>()

    val path = if (!Config.isProd) Path.of("ui/build") else Path.of("ui/public")
    assets("/", AssetsHandler(path, useIndexForUnknownPaths = true))

    context("/oauth") {
      register<OAuthUserProvider>(AuthUserProvider::class)
      register<GoogleOAuthClient>()
      annotated<OAuthRoutes>()
    }

    context("/api") {
      post("/js-error") { logger("js-error").error(rawBody) }

      before<AccessChecker>()

      annotated<ProjectRoutes>("/projects")
      annotated<CustomerRoutes>("/customers")
      annotated<UserRoutes>("/users")
      annotated<InvoiceRoutes>("/invoices")
      annotated<AuthRoutes>(annotations = listOf(Public()))
    }
    start()
  }
}
