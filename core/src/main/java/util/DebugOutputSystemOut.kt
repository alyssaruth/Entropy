package util

class DebugOutputSystemOut : DebugOutput {
    override fun append(text: String?) {
        print(text)
    }

    override fun clear() {}
}
