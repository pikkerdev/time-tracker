package merit

import klite.*
import klite.http.timeout
import klite.json.JsonHttpClient
import klite.json.JsonMapper
import merit.MeritInvoice.InvoiceRequest
import merit.MeritInvoice.InvoiceResponse
import java.net.http.HttpClient
import java.time.Duration.ofSeconds
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.time.Duration.Companion.seconds

class MeritClient {
  init {
    Converter.use { LocalDate.parse(it.substringBefore("T")) }
    Converter.use { LocalDateTime.parse(it.substringBefore("Z")) }
  }
  private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
  private val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd000000")
  private val json = JsonMapper(keys = Capitalize, values = object: ValueConverter<Any?>() {
    override fun to(o: Any?) = when(o) {
      is LocalDate -> dateFormat.format(o)
      else -> o
    }
  })
  private val http = JsonHttpClient("https://aktiva.merit.ee/api",
    http = HttpClient.newBuilder().connectTimeout(ofSeconds(30)).build(),
    json = json, maxLoggedLen = 4000,
    retryCount = 1, retryAfter = 5.seconds, reqModifier = { timeout(20.seconds) })
  private val apiId = Config["MERIT_API_ID"]
  private val signatureAlgorithm = "HmacSHA256"
  private val apiKey = SecretKeySpec(Config["MERIT_API_KEY"].toByteArray(), signatureAlgorithm)

  suspend fun send(invoice: MeritInvoice): MeritInvoice.Response {
    val timestamp = dateTimeFormat.format(LocalDateTime.now())
    val json = json.render(invoice)
    return http.post("/v1/sendinvoice?" + authParams(timestamp, json), json)
  }

  suspend fun invoice(req: InvoiceRequest): InvoiceResponse {
    val timestamp = dateTimeFormat.format(LocalDateTime.now())
    val json = json.render(req)
    return http.post("/v2/getinvoice?" + authParams(timestamp, json), json)
  }

  private fun authParams(timestamp: String, json: String): String {
    return urlEncodeParams(mapOf("ApiId" to apiId, "timestamp" to timestamp, "signature" to signature(timestamp, json)))
  }

  private fun signature(timestamp: String, json: String): String {
    val message = (apiId + timestamp + json).toByteArray()
    return Mac.getInstance(signatureAlgorithm).run {
      init(apiKey)
      doFinal(message).base64Encode()
    }
  }
}
