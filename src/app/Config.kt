package app

import klite.Config
import klite.d

val vatRate = Config.optional("VAT_RATE", "0.24").d

