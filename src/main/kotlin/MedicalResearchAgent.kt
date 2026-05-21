import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.ExitTool
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.agents.ext.tool.file.ListDirectoryTool
import ai.koog.agents.ext.tool.file.ReadFileTool
import ai.koog.agents.ext.tool.file.WriteFileTool
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.params.LLMParams
import ai.koog.rag.base.files.JVMFileSystemProvider
import java.time.LocalDateTime

val toolRegistry = ToolRegistry {
    tool(SayToUser)
    tool(AskUser)
    tool(ExitTool)
    tool(ReadFileTool(JVMFileSystemProvider.ReadOnly))
    tool(ListDirectoryTool(JVMFileSystemProvider.ReadOnly))
    tool(WriteFileTool(JVMFileSystemProvider.ReadWrite))
}

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

private fun getHomeDirectory(): String {
    return System.getProperty("user.home") ?: System.getenv("HOME")
}

private val agentConfig = AIAgentConfig(
    prompt = prompt(
        id = "medical-research-assistant",
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
            When asked to save the results to disk or local file use the WriteFileTool to do so. 
            When saving files auto generate the file name based on the context. 
            Saved files should always be stored at "${getHomeDirectory()}" unless a specific path is provided by the user.
            The current date and time is ${LocalDateTime.now()}.
        """.trimIndent())
    },
    model,
    maxAgentIterations = 5
)

val medicalResearchAgent = AIAgent(
    promptExecutor = simpleOllamaAIExecutor(),
    agentConfig = agentConfig,
    toolRegistry = toolRegistry
)
