import React from 'react';

export default class Menu extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        console.log('location: ' + this.props.location);

        return (
            <nav className="navbar navbar-default navbar-fixed-top">
                <div className="container">
                    <div className="navbar-header">
                        <button type="button" className="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                            <span className="sr-only">Toggle navigation</span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                        </button>
                        <a className="navbar-brand" href="#">Recode</a>
                    </div>
                    <div id="navbar" className="navbar-collapse collapse">
                        <ul className="nav navbar-nav">
                            <li><a href="#">Getting started</a></li>
                            <li><a href="#/gettingstarted">Documentation</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        )
    }
}