package logging

interface ILogContextListener {
    fun contextUpdated(context: Map<String, Any?>)
}
