import { Component, useEffect, useState } from "react";
import {
    useParams,
  } from "react-router-dom";
  import useWebSocket from 'react-use-websocket';
  import { CommentSection } from "react-comments-section";
import "react-comments-section/dist/index.css";
import {useJwt} from 'react-jwt';

  import { getFilm } from "../data";
  
  export default function FilmData(props) {

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
      onMessage: (msg) => updateComment(msg.data),
      shouldReconnect: (closeEvent) => true,
    });

    let [film,setFilm] = useState([]);
    let params = useParams();
    let [comments, setComments] =useState([]);
    let [enableCommentPost,setEnableCommentPost] = useState({"currentUser":null})

    const token = localStorage.token;
    const { decodedToken, isExpired, reEvaluateToken } = useJwt(token);


    useEffect(()=>{
      checkLogin()},[])

    useEffect(()=>{
      checkLogin();
      getFilm(params.movieId)
        .then(f=>{
          getFilmComments(f.comments);
          if(f && f!==""){
            setFilm(f);
          }
        });
      
    },[params.movieId]);

    const updateComment =(msg)=> {
        if(film && film!==""){
            if(msg && msg!==''){
                var updatedCommentJson = JSON.parse(msg);
                if(film.id === updatedCommentJson.id){
                  getFilmComments(updatedCommentJson.comments);
                }
            }
          }
    }
    
    const getFilmComments = (commentList) => {
      const x=commentList.map(x=> {
        var arr = {...x};
        arr.replies=[];
        arr.avatarUrl =`https://ui-avatars.com/api/name=${arr.fullName}&background=random`
        return arr;
      });
      setComments(x);
    }

    const checkLogin = () => {
      console.log("isExpired",isExpired, "localStorage.isAuthenticated",localStorage.isAuthenticated)
      let x= !(isExpired|!localStorage.isAuthenticated);
      setEnableCommentPost(x);
      console.log("Enable:",x);
    }

    return (
      <main style={{ padding: "1rem" }}>
        <h2>Movie Name: {film.name}</h2>
        <p>
          Ticket Price: {film.price}
        </p>
        <p>Rating: {film.rating}</p>

       <CommentSection
                    currentUser={null}
                    logIn={{
                      loginLink: "http://localhost:8080/",
                      signupLink: "http://localhost:3000/signup"
                    }}
                    removeEmoji
                    commentData={comments}
                  /> 
           

      </main>
    );
  }