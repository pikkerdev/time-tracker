package locations

import klite.json.JsonMapper
import klite.json.parse

@JvmInline value class CountryCode(val code: String) {
  init {
    require(code.length == 2)
  }

  companion object {
    val EE = CountryCode("EE")
    val FI = CountryCode("FI")
    val LV = CountryCode("LV")
    val LT = CountryCode("LT")
    val PL = CountryCode("PL")
    val DE = CountryCode("DE")

    private val configs = JsonMapper().parse<Map<CountryCode, CountryConfig>>(javaClass.getResourceAsStream("/countries.json")!!)
  }
}

data class CountryConfig(val phoneAreaCode: String) {}
