import React from 'react'
import ReactDOM from 'react-dom'
import {hashHistory, Route, Router} from 'react-router'

import Menu from './components/Menu.jsx'
import MainPage from './pages/MainPage.jsx'

ReactDOM.render(<Menu />, document.getElementById("menu-container"));

ReactDOM.render((<MainPage />), document.getElementById("main-container"));
