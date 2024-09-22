package http

import util.EncryptionUtil

object LegacyConstants {
    const val SYMMETRIC_KEY_STR = "XkYgcBFXbRl+8A8g8hyvOw=="
    val SYMMETRIC_KEY = EncryptionUtil.reconstructKeyFromString(SYMMETRIC_KEY_STR)
}
