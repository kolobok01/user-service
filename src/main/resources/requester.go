package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
)

func handleJSON(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Only POST method is allowed", http.StatusMethodNotAllowed)
		return
	}

	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		http.Error(w, "Failed to read request body", http.StatusBadRequest)
		return
	}
	defer r.Body.Close()

	var jsonData json.RawMessage
	if err := json.Unmarshal(body, &jsonData); err != nil {
		http.Error(w, "Invalid JSON", http.StatusBadRequest)
		return
	}

	fmt.Println("Received JSON:", string(jsonData))

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(map[string]string{"message": "JSON received successfully"})
}

func main() {
	http.HandleFunc("/json", handleJSON)
	fmt.Println("Server started on :8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}


{{ define "block.alerts" -}}
{
"alerts": [
{{ range $a, $el := .Alerts }}
{{if $a}},
{{end}}
{
  {{ range $i, $e := .Labels.SortedPairs }}
{{if $i}},{{end}}"{{ .Name }}": "{{ .Value }}"
{{end}}
}
{{ end }}
]
}
{{ end -}}
