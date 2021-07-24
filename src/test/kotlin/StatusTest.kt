import koodies.test.shouldBe
import koodies.test.shouldContain
import koodies.test.shouldNotContain
import koodies.time.Now
import koodies.time.minus
import koodies.time.minutes
import koodies.time.plus
import koodies.time.seconds
import kotlinx.browser.document
import org.w3c.dom.Element
import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration

class StatusTest {

    @Test
    fun shouldDeserialize() {
        val timestamp = Now - 37.minutes + 9.seconds

        val status = Status.fromJson("""
            {
              name: "busy",
              task: "ABC-123",
              duration: "PT50M",
              timestamp: "${timestamp.toISOString()}",
              email: "john.doe@example.com"
            }
        """.trimIndent())

        assertEquals(Status(
            name = "busy",
            task = "ABC-123",
            duration = 50.minutes,
            timestamp = timestamp,
            email = "john.doe@example.com",
        ), status)
    }

    @Test
    fun shouldDeserializeMinimal() {
        val status = Status.fromJson("""
            {
              name: "busy"
            }
        """.trimIndent())

        assertEquals(Status(name = "busy"), status)
    }

    @Test
    fun shouldAppendStatus() {
        createStatus(
        ).rendered shouldBe """
            <div class="status nes-container is-centered">
                <h1 class="status__task">
                    <span>ABC-123</span>
                </h1>
                <div class="status__status">
                    <div class="status__name nes-balloon from-right">
                        <span class="nes-text is-error">busy</span>
                    </div>
                    <img alt="avatar" src="https://www.gravatar.com/avatar/8eb1b522f60d11fa897de1dc6351b7e8?s=64" class="status__avatar nes-avatar is-large">
                </div>
                <progress class="status__passed nes-progress is-warning" max="3000" value="2211"></progress>
                <div class="status__remaining">
                    <small>remaining</small>
                    <div>13m</div>
                </div>
            </div>
        """.trimIndent()
    }

    @Test
    fun shouldAppendCompletedStatus() {
        createStatus(
            timestamp = Date(0),
        ).rendered
            .shouldContain(
                """<span class="nes-text">busy</span>""",
                """<progress class="status__passed nes-progress is-success" max="1" value="1"></progress>""",
                """<div class="status__remaining">Done</div>""",
            )
            .shouldNotContain(
                "remaining"
            )
    }

    @Test
    fun shouldAppendStatusWithoutTask() {
        createStatus(
            task = null,
        ).rendered.shouldContain("""<span>Status</span>""")
    }

    @Test
    fun shouldAppendStatusWithoutDuration() {
        createStatus(
            duration = null,
        ).rendered shouldBe """
            <div class="status nes-container is-centered">
                <h1 class="status__task">
                    <span>ABC-123</span>
                </h1>
                <div class="status__status">
                    <div class="status__name nes-balloon from-right">
                        <span class="nes-text is-error">busy</span>
                    </div>
                    <img alt="avatar" src="https://www.gravatar.com/avatar/8eb1b522f60d11fa897de1dc6351b7e8?s=64" class="status__avatar nes-avatar is-large">
                </div>
            </div>
        """.trimIndent()
    }

    @Test
    fun shouldAppendStatusWithoutTimestamp() {
        createStatus(
            timestamp = null,
        ).rendered shouldBe """
            <div class="status nes-container is-centered">
                <h1 class="status__task">
                    <span>ABC-123</span>
                </h1>
                <div class="status__status">
                    <div class="status__name nes-balloon from-right">
                        <span class="nes-text is-error">busy</span>
                    </div>
                    <img alt="avatar" src="https://www.gravatar.com/avatar/8eb1b522f60d11fa897de1dc6351b7e8?s=64" class="status__avatar nes-avatar is-large">
                </div>
            </div>
        """.trimIndent()
    }

    @Test
    fun shouldAppendStatusWithoutEmail() {
        createStatus(
            email = null,
        ).rendered.shouldContain(
            """<img alt="avatar" src="$DINOSAUR" class="status__avatar nes-avatar is-large">""",
        )
    }

    @Test
    fun shouldAppendStatusWithNameOnly() {
        Status(
            name = "busy",
        ).rendered shouldBe """
            <div class="status nes-container is-centered">
                <h1 class="status__task">
                    <span>Status</span>
                </h1>
                <div class="status__status">
                    <div class="status__name nes-balloon from-right">
                        <span class="nes-text is-error">busy</span>
                    </div>
                    <img alt="avatar" src="$DINOSAUR" class="status__avatar nes-avatar is-large">
                </div>
            </div>
        """.trimIndent()
    }
}

fun createStatus(
    name: String = "busy",
    task: String? = "ABC-123",
    duration: Duration? = 50.minutes,
    timestamp: Date? = Now - 36.minutes - 51.seconds,
    email: String? = "john.doe@example.com",
) = Status(
    name,
    task,
    duration,
    timestamp,
    email,
)

private val Status.rendered: Element
    get() {
        val container = document.createElement("div")
        appendTo(container)
        return container
    }
