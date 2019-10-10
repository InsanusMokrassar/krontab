import com.insanusmokrassar.krontab.builder.buildSchedule
import com.insanusmokrassar.krontab.executeOnce
import com.soywiz.klock.DateTime
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        buildSchedule {
            seconds {
                from(5) every 10
                from(0) upTo 5
            }
            minutes {
                at(30)
                at(59)
            }
        }.also {
            println(it)
        }.executeOnce {
            println("Done: ${DateTime.now()}")
        }
    }
}
