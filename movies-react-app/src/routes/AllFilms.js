import { useEffect, useState } from "react";
import {
  NavLink,
  Outlet,
  useSearchParams,
} from "react-router-dom";
import { getAllFilms } from "../data";
  import useWebSocket from 'react-use-websocket';


export default function AllFilms() {
  let [films, setFilms] = useState([]);
  let [searchParams, setSearchParams] = useSearchParams();


  const socketUrl = 'ws://localhost:8080/ws/films';
    const {
      sendMessage,
      sendJsonMessage,
      lastMessage,
      lastJsonMessage,
      readyState,
      getWebSocket,
    } = useWebSocket(socketUrl, {
      onOpen: (x) => console.log('socket opened',x),
      onMessage: (msg) => updateFilmsList(msg.data),
      //Will attempt to reconnect on all close events, such as server shutting down
      shouldReconnect: (closeEvent) => true,
    });

    const updateFilmsList = (msg1)=> {
      const newFilm = JSON.parse(msg1);
      const found = films.find(s=>s.id===newFilm.id);
      if(!found){
        films.push(newFilm);
      }
    }

  useEffect(()=>{
    getAllFilms().then(x=>{
      setFilms(x);
    });
  },[]);

  return (
    <div style={{ display: "flex" }}>
      <nav
        style={{
          borderRight: "solid 10px",
          padding: "1rem",
        }}
      >

        {films
          .filter((invoice) => {
            let filter = searchParams.get("filter");
            if (!filter) return true;
            let name = invoice.name.toLowerCase();
            return name.startsWith(filter.toLowerCase());
          })
          .map((invoice) => (
            <NavLink
              style={({ isActive }) => ({
                display: "block",
                margin: "1rem 0",
                color: isActive ? "red" : "",
              })}
              to={`/films/${invoice.name}`}
              state={{"hello":"world"}}
              key={invoice.id}
            >
              {invoice.name}
            </NavLink>
          ))}
      </nav>
      <Outlet />
    </div>
  );
}