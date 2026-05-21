import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.params.LLMParams
import java.time.LocalDateTime

private val model = LLModel(
    provider = LLMProvider.Ollama,
    id = "qwen3.6:35b",
    capabilities = listOf(
        LLMCapability.Temperature,
        LLMCapability.Tools,
        LLMCapability.Schema.JSON.Standard,
        LLMCapability.ToolChoice
    )
)

private val agentConfig = AIAgentConfig(
    prompt = prompt(
        id = "assistant",
        params = LLMParams(
            temperature = 0.7
        )
    ) {
        system("""
                    You are an medical research expert. Be courteous. 
                    Answer the user questions to the best of your knowledge.
                    Remind the user that the information provided doesn't represent medical advise. 
                    Ask the user to consult a physician for health related questions and concerns.
                    Produce the results in markdown formatted text.
                    The current date and time is ${LocalDateTime.now()}.
                """.trimIndent())
    },
    model,
    maxAgentIterations = 10
)

val medicalResearchAgent = AIAgent(
    promptExecutor = simpleOllamaAIExecutor(),
    agentConfig
)
