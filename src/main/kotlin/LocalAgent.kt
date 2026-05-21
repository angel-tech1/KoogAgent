import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.context.AIAgentContext
import ai.koog.agents.core.agent.session.AIAgentRunSession
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolRegistry
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
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDateTime
import java.util.*

class LocalAgent(
    override val id: String,
    localModelName: String,
    systemPrompt: String,
    temperature: Double = 0.7,
    maxAgentIterations: Int = 10,
    val bannerFilePath: String? = null,
    additionalTools: List<Tool<*, *>> = emptyList()
): AIAgent<String, String>() {

    val logger = KotlinLogging.logger {}

    private fun getHomeDirectory(): String {
        return System.getProperty("user.home") ?: System.getenv("HOME")
    }

    private fun getCurrentTime(): String = LocalDateTime.now().toString()

    fun printBanner() {
        if (bannerFilePath == null) {
            println("Loading agent $id")
            return
        }

        try {
            println(this::class.java.getResourceAsStream(bannerFilePath)?.bufferedReader()?.readText())
        } catch (e: Throwable) {
            logger.warn("Failed to load model $id banner", e)
        }
    }

    fun startSession(): String {
        val sessionId = UUID.randomUUID().toString()
        createSession(sessionId)
        println("Started a new session: $sessionId")
        return sessionId
    }

    private val toolRegistry = ToolRegistry {
        tool(SayToUser)
        //tool(AskUser)
        tool(ExitTool)
        tool(ReadFileTool(JVMFileSystemProvider.ReadOnly))
        tool(ListDirectoryTool(JVMFileSystemProvider.ReadOnly))
        tool(WriteFileTool(JVMFileSystemProvider.ReadWrite))
        tools(additionalTools)
    }

    private val model = LLModel(
        provider = LLMProvider.Ollama,
        id = localModelName,
        capabilities = listOf(
            LLMCapability.Temperature,
            LLMCapability.Tools,
            LLMCapability.Schema.JSON.Standard,
            LLMCapability.ToolChoice
        )
    )

    override val agentConfig = AIAgentConfig(
        prompt = prompt(
            id,
            params = LLMParams(
                temperature
            )
        ) {
            system("""
            $systemPrompt
            The current date and time is ${getCurrentTime()}.
            Produce the results as markdown formatted text.
            When receiving a response in JSON format reformat it as human readable markdown format.
            When asked to save the results to disk or local file use the WriteFileTool to do so.
            When saving files auto generate the file name based on the context of the conversation.
            Saved files should be stored by default in the user's home directory located at "${getHomeDirectory()}".
            Make sure the file path passed to the __write_file__ tool is an absolute path and starts with "${getHomeDirectory()}".
            Acknowledge user feedback when received. Reply to the feedback in a briefly manner.
            When the user request is unclear avoid replying with random data and ask the user for more information.
            Avoid reading or writing local files unless the user explicitly requests it.
        """.trimIndent())
        },
        model,
        maxAgentIterations
    )

    private val agent = AIAgent(
        promptExecutor = simpleOllamaAIExecutor(),
        agentConfig = agentConfig,
        toolRegistry = toolRegistry
    )

    override suspend fun run(agentInput: String, sessionId: String?): String {
        return agent.run(agentInput, sessionId)
    }

    override fun createSession(sessionId: String?): AIAgentRunSession<String, String, out AIAgentContext> {
        return agent.createSession(sessionId)
    }

    override suspend fun close() {
        agent.close()
    }
}
