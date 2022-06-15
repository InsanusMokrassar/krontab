package dev.inmo.krontab.utils

import dev.inmo.krontab.buildSchedule
import kotlin.test.Test
import kotlin.test.assertNotNull

class InfinityLoopCheckTest {
    @Test
    fun absenceOfInfinityLoopCheckTest() {
        runTest {
            assertNotNull(buildSchedule("0 0 0 1 *").next())
        }
    }
}
