import styled from "@emotion/styled";
import React from "react";
import Bold from "../components/bold";
import Italic from "../components/italic";
import TeamView from "../components/teamview";
import { colors } from "../style";

const Dashboard: React.FunctionComponent = () => {
  return (
    <PageContainer>
      <Header>
        <PageTitle>
          <Bold>Team Tracking View</Bold>
        </PageTitle>
        <InstructionsContainer>
          This is a read-only application, in order to produce data to be viewed
          bellow, take a look at the{" "}
          <Bold>
            <Italic>team-simulator project</Italic>
          </Bold>
        </InstructionsContainer>
      </Header>
      <Body>
        <TeamView />
      </Body>
    </PageContainer>
  );
};

const PageContainer = styled.div({
  backgroundColor: colors.background,
  width: "100vw",
  height: "100vh",
});

const Header = styled.div({
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  minHeight: 80,
});

const InstructionsContainer = styled.div({
  marginTop: "2.5rem",
});

const PageTitle = styled.div({
  marginTop: "2.5rem",
  color: colors.primary,
  fontSize: "1.5rem",
});

const Body = styled.div({
  display: "flex",
  justifyContent: "center",
  paddingTop: "8rem",
  paddingBottom: "8rem",
});

export default Dashboard;
