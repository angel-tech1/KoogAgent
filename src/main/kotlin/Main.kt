import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    medicalResearchAgent.printBanner()
    Commands.printAvailableCommands()

    var sessionId = medicalResearchAgent.startSession()
    var userInput = askUser("How can I help you today?")

    while (userInput != Command.QUIT.key) {
        if (Commands.isValidCommand(userInput)) {
            if(Commands.valueOf(userInput) == Command.CLEAR) {
                sessionId = medicalResearchAgent.startSession()
            } else {
                Commands.run(Commands.valueOf(userInput))
            }
            userInput = askUser()
        } else {
            println("Please wait a moment...")
            val result = medicalResearchAgent.run(userInput, sessionId)

            println(result)
            userInput = askUser("Do you have any other question?")
        }
    }
}
