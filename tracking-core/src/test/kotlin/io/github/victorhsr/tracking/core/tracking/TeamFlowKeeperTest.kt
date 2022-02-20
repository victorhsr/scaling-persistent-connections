package io.github.victorhsr.tracking.core.tracking

import io.github.victorhsr.tracking.core.Location
import io.github.victorhsr.tracking.core.TeamFlowKeeper
import io.github.victorhsr.tracking.core.TrackingData
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TeamFlowKeeperTest {

    @Test
    fun `should create a flow for a team, if it doesn't exist yet, and retrieve the same flow if a new call is made using the same team`() =
        runBlocking {

            val teamFlowKeeper = TeamFlowKeeper()
            val team = "SOME_TEAM"
            val createdTeamFlow = teamFlowKeeper.getTeamFlow(team)
            val retrievedFlow = teamFlowKeeper.getTeamFlow(team)

            assertEquals(createdTeamFlow, retrievedFlow)
        }

    @Test
    fun `should return null if asked to get a flow that doesn't exists yet, and the create parameter is set to false`() =
        runBlocking {

            val teamFlowKeeper = TeamFlowKeeper()

            val team = "SOME_TEAM"
            val flow = teamFlowKeeper.getTeamFlow(team, false)
            assertNull(flow)
        }

    @Test
    fun `should remove the flow from the keeper if it's last consumer stops`() = runBlocking {

        val teamFlowKeeper = TeamFlowKeeper()

        val team = "SOME_TEAM"
        val flow = teamFlowKeeper.getTeamFlow(team)!!

        val subscriberJobOne = launch { flow.collect { println("Collecting tracking-data") } }
        delay(200)

        val teamFlowWhenItStillHaveConsumers = teamFlowKeeper.getTeamFlow(team, false)
        assertNotNull(teamFlowWhenItStillHaveConsumers)

        subscriberJobOne.cancelAndJoin()
        delay(200)

        val teamFlowWithNoConsumers = teamFlowKeeper.getTeamFlow(team, false)
        assertNull(teamFlowWithNoConsumers)
    }

    @Test
    fun `should keep the flow in the keeper if some of its consumers stops and there are still active consumers`() =
        runBlocking {

            val teamFlowKeeper = TeamFlowKeeper()

            val team = "SOME_TEAM"
            val flow = teamFlowKeeper.getTeamFlow(team)!!

            val subscriberJobOne = launch { flow.collect { println("Collecting tracking-data 1") } }
            val subscriberJobTwo = launch { flow.collect { println("Collecting tracking-data 2") } }
            delay(200)

            subscriberJobOne.cancelAndJoin()
            delay(200)

            val teamFlow = teamFlowKeeper.getTeamFlow(team, false)
            assertNotNull(teamFlow)
            subscriberJobTwo.cancelAndJoin()
        }

    @Test
    fun `should be sure that removing of one flow will not interfere in other ones`() = runBlocking {

        val teamFlowKeeper = TeamFlowKeeper()

        val teamOne = "TEAM_ONE"
        val teamTwo = "TEAM_TWO"
        val teamThree = "TEAM_THREE"

        val flowOne = teamFlowKeeper.getTeamFlow(teamOne)!!
        val flowTwo = teamFlowKeeper.getTeamFlow(teamTwo)!!
        val flowThree = teamFlowKeeper.getTeamFlow(teamThree)!!

        val flowOneJobs = listOf(
            launch { flowOne.collect { println("Collecting flow-one") } },
            launch { flowOne.collect { println("Collecting flow-one") } },
            launch { flowOne.collect { println("Collecting flow-one") } }
        )

        val flowTwoJobs = listOf(
            launch { flowTwo.collect { println("Collecting flow-two") } },
            launch { flowTwo.collect { println("Collecting flow-two") } },
            launch { flowTwo.collect { println("Collecting flow-two") } }
        )

        val flowThreeJobs = listOf(
            launch { flowThree.collect { println("Collecting flow-three") } },
        )

        delay(200)
        flowOneJobs.forEach { job -> job.cancelAndJoin() }
        flowTwoJobs.first().cancelAndJoin()
        delay(200)

        assertNull(teamFlowKeeper.getTeamFlow(teamOne, false))
        assertNotNull(teamFlowKeeper.getTeamFlow(teamTwo, false))
        assertNotNull(teamFlowKeeper.getTeamFlow(teamThree, false))

        flowTwoJobs.forEach { job -> job.cancelAndJoin() }
        delay(200)
        assertNull(teamFlowKeeper.getTeamFlow(teamOne, false))
        assertNull(teamFlowKeeper.getTeamFlow(teamTwo, false))
        assertNotNull(teamFlowKeeper.getTeamFlow(teamThree, false))

        flowThreeJobs.forEach { job -> job.cancelAndJoin() }
        delay(200)
        assertNull(teamFlowKeeper.getTeamFlow(teamOne, false))
        assertNull(teamFlowKeeper.getTeamFlow(teamTwo, false))
        assertNull(teamFlowKeeper.getTeamFlow(teamThree, false))
    }

    @Test
    fun `should not create a new flow instance if we try to push some tracking data and its team has no active listeners`() =
        runBlocking {
            val team = "team_one"
            val teamFlowKeeper = TeamFlowKeeper()
            teamFlowKeeper.pushTrackingData(TrackingData("some-id",
                "some-worker-id",
                team,
                Location("0", ""),
                LocalDateTime.now()))

            assertNull(teamFlowKeeper.getTeamFlow(team, false))
        }

}