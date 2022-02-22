import { Global } from "@emotion/react";

export const colors = {
  primary: "#f6f6f6",
  secondary: "#dddddd",
  background: "#0d1016",
  text: "#e7e7e7",
  textBlack: "#0d1016",
};

const GlobalStyles = () => (
  <Global
    styles={{
      [["html", "body"]]: {
        height: "100%",
      },
      body: {
        margin: 0,
        padding: 0,
        fontFamily: "'Source Sans Pro', sans-serif",
        backgroundColor: colors.background,
        color: colors.text,
      },
      "*": {
        boxSizing: "border-box",
      },
    }}
  />
);

export default GlobalStyles;
