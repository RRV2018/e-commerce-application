    // HeaderWrapper.js
import React from "react";
import { useLocation } from "react-router-dom";
import Header from "./Header";

const HeaderWrapper = ({ children }) => {
  const location = useLocation();

  // Do NOT show Header on login page
  const hideHeaderOnPaths = ["/login"];

  const showHeader = !hideHeaderOnPaths.includes(location.pathname);

  return (
    <div>
      {showHeader && <Header />}
      <main>{children}</main>
    </div>
  );
};

export default HeaderWrapper;
