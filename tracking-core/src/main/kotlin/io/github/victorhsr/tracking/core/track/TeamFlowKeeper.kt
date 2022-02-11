package io.github.victorhsr.tracking.core.track

import io.github.victorhsr.tracking.core.commons.runWithLock
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import org.slf4j.LoggerFactory

class TeamFlowKeeper {

    private val teamToFlowMap = mutableMapOf<String, MutableSharedFlow<Track>>()
    private val lock = Mutex(false)
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("flow-keeper"))

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TeamFlowKeeper::class.java)
    }

    suspend fun getTeamFlow(team: String, createIfNotExist: Boolean = true): MutableSharedFlow<Track>? {
        return runWithLock(this.lock) {
            if (!this.teamToFlowMap.containsKey(team)) {
                LOGGER.info("There is no flow for team {}, createIfNotExist {}", team, createIfNotExist)

                if (createIfNotExist) {
                    val flow = buildTeamFlow(team)
                    teamToFlowMap[team] = flow
                    return@runWithLock flow
                }
            }

            this.teamToFlowMap[team]
        }
    }

    suspend fun pushTrack(track: Track) {
        val flow = this.getTeamFlow(track.team)

        if (flow == null) {
            LOGGER.info("There are no consumers for team {}, skipping...")
            return
        }

        flow.emit(track)
    }


    private fun buildTeamFlow(team: String): MutableSharedFlow<Track> {
        LOGGER.info("Creating flow for team {}", team)

        var alreadyHadItsFirstSubscriber = false

        val flow = MutableSharedFlow<Track>(0, Int.MAX_VALUE)
        flow.subscriptionCount
            .map { count ->
                LOGGER.info("There are {} listener(s) for team {}", count, team)
                if (count > 0 && !alreadyHadItsFirstSubscriber) {
                    alreadyHadItsFirstSubscriber = true
                }

                count >= 1
            }.distinctUntilChanged().onEach { isActive ->
                if (!isActive && alreadyHadItsFirstSubscriber) {
                    LOGGER.info("There are no active consumers for team {} flow, removing it from team-flow-manager",
                        team)
                    runWithLock(lock) {
                        this.teamToFlowMap.remove(team)
                    }
                }
            }.launchIn(this.scope)

        return flow
    }

}
