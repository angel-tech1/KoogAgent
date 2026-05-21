import Command.entries
import ai.koog.agents.core.agent.GraphAIAgent
import java.util.*

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

    fun run(command: Command, agent: GraphAIAgent<String, String>) {
        when (command) {
            Command.QUIT -> println("Exiting")
            Command.HELP -> printAvailableCommands()
            Command.CLEAR -> {
                val newSessionId = UUID.randomUUID().toString()
                agent.createSession(newSessionId)
                println("New session: $newSessionId")
            }
            else -> println() // do nothing
        }
    }
}
