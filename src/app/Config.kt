package app

import java.math.BigDecimal
import klite.Config

val vatRate: BigDecimal = Config.optional("VAT_RATE", "0.24").toBigDecimal()

