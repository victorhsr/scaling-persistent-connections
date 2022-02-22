import styled from "@emotion/styled";
import React from "react";
import { TrackingStrategy } from "../../service/trackingService";
import { colors } from "../../style";
import Bold from "../bold";
import Underline from "../underline";

interface TeamDetailsProps {
  team: string;
  totalWorkers: number;
  refreshTime: number;
  strategy: TrackingStrategy;
}

const TeamDetails: React.FunctionComponent<TeamDetailsProps> = (props) => {
  const { team, totalWorkers, strategy, refreshTime } = props;

  return (
    <Container>
      <TeamContainer>
        <Bold>{team}</Bold>
      </TeamContainer>
      <WorkersInfoContainer>
        {totalWorkers} unique worker(s) have sent data in the last{" "}
        {refreshTime / 1000} seconds
      </WorkersInfoContainer>
      <StrategyContainer>
        <Underline>{strategy}</Underline> strategy
      </StrategyContainer>
    </Container>
  );
};

const Container = styled.div({
  position: "relative",
  display: "flex",
  flexDirection: "column",
  height: "100%",
  width: "15.5rem",
  background: colors.secondary,
  color: colors.textBlack,
  paddingLeft: "1rem",
  paddingRight: "1rem",
});

const TeamContainer = styled.div({
  textAlign: "center",
  marginTop: "2rem",
  marginBottom: "6rem",
});

const WorkersInfoContainer = styled.div({});

const StrategyContainer = styled.div({
  position: "absolute",
  textAlign: "center",
  bottom: "0",
  width: "100%",
  left: "0",
  marginBottom: "2rem",
});

export default TeamDetails;
