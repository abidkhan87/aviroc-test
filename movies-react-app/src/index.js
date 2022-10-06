import { render } from "react-dom";
import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";
import App from "./App";
import Signup from "./routes/Signup";
import AllFilms from "./routes/AllFilms";
import FilmData from "./routes/FilmData";
import './index.css';

const rootElement = document.getElementById("root");
render(
  <BrowserRouter>
    <Routes>
  <Route path="/" element={<App />}>
    <Route path="signup" element={<Signup />} />
    <Route path="films" element={<AllFilms />}>
    <Route
        index
        element={
          <main style={{ padding: "1rem" }}>
            <p>Select a Film</p>
          </main>
        }
      />
      <Route path=":movieId" element={<FilmData />} />
    </Route>
    <Route
      path="*"
      element={
        <main style={{ padding: "1rem" }}>
          <p>There's nothing here!</p>
        </main>
      }
    />
  </Route>
</Routes>
  </BrowserRouter>,
  rootElement
);