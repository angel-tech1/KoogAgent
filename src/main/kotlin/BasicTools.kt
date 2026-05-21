import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool

@Tool
@LLMDescription("Ask the user a question by sending it to stdout and return the answer from stdin")
fun askUser(
    @LLMDescription("Question from the agent") question: String? = null
): String {
    if (question != null) {
        println("\n$question")
    }
    print("> ")
    return readln()
}
