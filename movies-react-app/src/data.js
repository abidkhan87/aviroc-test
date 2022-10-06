const axios = require("axios");

  let movies = [
    {
        id:1,
      name: "Saving Private Ryan",
      rating: 1995,
      price: "$10,800",
      due: "12/05/1995",
    },
    {
        id:2,
      name: "Batman",
      rating: 2000,
      price: "$8,000",
      due: "10/31/2000",
    },
    {
        id:3,
      name: "Oceans Eleven",
      rating: 2003,
      price: "$9,500",
      due: "07/22/2003",
    },
    {
        id:4,
      name: "Titanic",
      rating: 1997,
      price: "$14,000",
      due: "09/01/1997",
    },

  ];
  
  export function getInvoices() {
    return movies;
  }

  export function getInvoice(movieId) {
    return movies.find(
      (movie) => movie.name === movieId
    );

  }

  export function deleteInvoice(movieId) {
    movies = movies.filter(
      (movie) => movie.name !== movieId
    );
  }

export const getFilm = (slugname)=>{
    let xx="";
    return axios
      .get(`http://localhost:8080/films/slug/${slugname}`)
      .then(res => {
        if (res.status === 200) {
            xx=res.data;
            return res.data;
        } else {
            this.setState({errors: { message: res.data.message }});
        }
    })
    .catch((err) => {
        console.log("error: ", err);
    });
  }


  export function getAllFilms(){
        return axios
          .get("http://localhost:8080/films")
          .then((res) => {
            if (res.status === 200) {
                return res.data;
            } else {
              this.setState({
                errors: { message: res.data.message }
              });
            }
          })
          .catch((err) => {
            console.log("error: ", err);
          });
      }
  