import { Outlet, Link } from "react-router-dom";
import React from "react";

class App extends React.Component{

  constructor(props) {
    super(props);
}

  render(){
    return (
      <div>
        <h1>Films Websockets</h1>
        <nav
          style={{
            borderBottom: "solid 1px",
            paddingBottom: "1rem",
          }}
        >
          <Link to="/films">All Films</Link> 
          {/* |{" "}
          <Link to="/signup">Expenses</Link> */}
        </nav>
        <Outlet />
      </div>
    );
  }
}

export default App;