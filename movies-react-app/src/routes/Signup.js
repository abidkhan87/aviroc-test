import React from 'react';
import SignUpContainer from "../SignUpContainer"
import MuiThemeProvider from "material-ui/styles/MuiThemeProvider";
import '../index.css';

class Signup extends React.Component {
    render() {
        return(
            <div>
                <MuiThemeProvider>
                    <SignUpContainer />
                </MuiThemeProvider>
            </div>
        );
    }
}

export default Signup;

