import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    Commands.printAvailableCommands()
    var userInput = askUser("How can I help you today?")

    while (userInput != Command.QUIT.key) {

        if (Commands.isValidCommand(userInput)) {
            Commands.run(Commands.valueOf(userInput), medicalResearchAgent)
            userInput = askUser("")
        } else {
            println("Please wait a moment...")
            val result = medicalResearchAgent.run(userInput)

            println(result)
            userInput = askUser("Do you have any other question?")
        }
    }
}
