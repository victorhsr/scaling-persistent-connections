import React from "react";
import ReactDOM from "react-dom";
import Dashboard from "./pages/dashboard";
import GlobalStyles from "./style";

ReactDOM.render(
  <React.StrictMode>
    <GlobalStyles />
    <Dashboard />
  </React.StrictMode>,
  document.getElementById("root")
);
