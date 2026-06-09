package app

import klite.Config
import klite.d

val vatRate = Config.optional("VAT_RATE", "0.24").d

val companyName = Config.optional("COMPANY_NAME", "Pikker Technology OÜ")
val companyAddress = Config.optional("COMPANY_ADDRESS", "Laeva tn 2, Tallinn 10111")
val companyPhone = Config.optional("COMPANY_PHONE", "+372 53064406")
