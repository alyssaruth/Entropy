package routes.dev

enum class DevCommand(val value: String) {
    DUMP_THREADS("threads"),
    DUMP_THREAD_STACKS("stacks"),
    DUMP_USERS("users"),
    DUMP_POOL_STATS("pool stats"),
    DUMP_MEMORY("memory"),
    FORCE_GC("gc"),
}
