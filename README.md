[![Build Status](https://github.com/opatry/ticktick-kt/actions/workflows/Build.yml/badge.svg)](https://github.com/opatry/ticktick-kt/actions/workflows/Build.yml)

# TickTick REST API Kotlin bindings

Kotlin binding for [TickTick REST API](https://developer.ticktick.com/api#/openapi) using [Ktor Http Client](https://ktor.io/) and [Gson](https://github.com/google/gson) as Json marshaller.

## Example

```kotlin
runBlocking {
    val permissions = TickTickServiceAuthenticator.Permission.entries
    val config = HttpTickTickServiceAuthenticator.ApplicationConfig(
        redirectUrl = "http://localhost:8888",
        clientId = System.getenv("TICKTICK_API_CLIENT_ID"),
        clientSecret = System.getenv("TICKTICK_API_CLIENT_SECRET"),
    )
    val authenticator: TickTickServiceAuthenticator = HttpTickTickServiceAuthenticator(config)

    val code = authenticator.authorize(permissions) { url ->
        withContext(Dispatchers.IO) {
            Desktop.getDesktop().browse(URI.create(url))
        }
    }

    val token = authenticator.getToken(code, permissions)

    val httpClient = HttpClient(CIO) {
        CurlUserAgent()
        install(ContentNegotiation) {
            gson()
        }
        install(Auth) {
            bearer {
                sendWithoutRequest { true }
                loadTokens {
                    BearerTokens(token.accessToken, "")
                }
            }
        }
        defaultRequest {
            url("https://api.ticktick.com")
        }
    }
    val tickTickService: TickTickService = HttpTickTickService(httpClient)

    val projects = tickTickService.getProjects()
    if (projects.isEmpty()) {
        println("No project found, creating one")
        val project = tickTickService.createProject(ProjectCreationRequest("My whole new project"))
        println("Project ${project.name} (#${project.id}) created")
    } else {
        projects.sortedBy(Project::sortOrder).forEach { project ->
            println("Tasks of project ${project.name} (#${project.id})")
            tickTickService.getProjectData(project.id).tasks
                .sortedWith(compareBy(Task::status, Task::sortOrder))
                .forEach { task ->
                    println("\t${task.status} Task: ${task.title} ${task.priority} (#${task.id})")
                    task.items
                        ?.sortedWith(compareBy(ChecklistItem::status, ChecklistItem::sortOrder))
                        ?.forEach { checklistItem ->
                            println("\t\t${checklistItem.status} ${checklistItem.title} (#${checklistItem.id})")
                        }
                }
        }
    }
}
```