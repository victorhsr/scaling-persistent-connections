import styled from "@emotion/styled";
import React, { useState } from "react";
import { map, Observable } from "rxjs";
import trackingManager, {
  TrackingData,
  TrackingStrategy,
} from "../../service/trackingService";
import Buttom from "../buttom";
import TeamMap from "./TeamMap";

const TeamView: React.FunctionComponent = () => {
  const redisTeam = "team_one";
  const kafkaTeam = "team_two";

  const [activeStreamInfo, setActiveStreamInfo] = useState({
    strategy: TrackingStrategy.KAFKA,
    team: kafkaTeam,
  });

  function prepareStream(stream: Observable<TrackingData>) {
    return stream.pipe(
      map((trackingData) => {
        return {
          id: trackingData.workerId,
          latitude: Number(trackingData.location.latitude),
          longitude: Number(trackingData.location.longitude),
        };
      })
    );
  }

  function getStream() {
    return prepareStream(
      trackingManager.getTrackingStream(
        activeStreamInfo.team,
        activeStreamInfo.strategy
      )
    );
  }

  function changeActiveStrategy(newStrategy: TrackingStrategy) {
    trackingManager.disposeTracking(
      activeStreamInfo.team,
      activeStreamInfo.strategy
    );

    setActiveStreamInfo({
      strategy: newStrategy,
      team: newStrategy === TrackingStrategy.REDIS ? redisTeam : kafkaTeam,
    });
  }

  return (
    <Container>
      <div>
        <Buttom
          title="Change to REDIS strategy"
          onClick={() => changeActiveStrategy(TrackingStrategy.REDIS)}
          disabled={activeStreamInfo.strategy === TrackingStrategy.REDIS}
        >
          Redis ({redisTeam})
        </Buttom>
        <Buttom
          title="Change to KAFKA strategy"
          onClick={() => changeActiveStrategy(TrackingStrategy.KAFKA)}
          disabled={activeStreamInfo.strategy === TrackingStrategy.KAFKA}
        >
          Kafka ({kafkaTeam})
        </Buttom>
      </div>
      <TeamMap
        dataStream={getStream()}
        team={activeStreamInfo.team}
        strategy={activeStreamInfo.strategy}
      />
    </Container>
  );
};

const Container = styled.div({
  width: "1000px",
  height: "500px",
});

export default TeamView;
