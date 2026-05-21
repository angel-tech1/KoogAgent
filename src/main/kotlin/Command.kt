import Command.entries

enum class Command(val key: String, val description: String) {
    QUIT("/q", "Quit"),
    HELP("/?", "Help"),
    CLEAR("/clear", "Clear conversation context"),
    VOID("/void", "No effect");
}

object Commands {

    fun valueOf(userInput: String): Command {
        return entries.firstOrNull {
            userInput.lowercase().startsWith(it.key)
        } ?: Command.VOID
    }

    fun isValidCommand(userInput: String): Boolean {
        return userInput.trim().startsWith("/") &&
                Command.entries.map { it.key }.contains(userInput.lowercase())
    }

    fun printAvailableCommands() {
        println("Available commands:")
        Command.entries.filter { it != Command.VOID }.forEach {
            println("${it.key}\t${it.description}")
        }
    }

    fun run(command: Command) {
        when (command) {
            Command.QUIT -> println("Exiting")
            Command.HELP -> printAvailableCommands()
            else -> {}
        }
    }
}
