package main

import(
	"log"
	"fmt"
	"net/http"
)

func defaultHandler(w http.ResponseWriter, r *http.Request) {

	

	fmt.Println("defaultHandler got request: ");
	fmt.Println(r.URL);
}

func main() {
	http.HandleFunc("/", defaultHandler)
	err := http.ListenAndServe(":5555", nil)
	if err != nil {
		log.Fatal("Failed to listen port 5555", err)
	} else {
		fmt.Println("Start serving on port 5555")
	}
}