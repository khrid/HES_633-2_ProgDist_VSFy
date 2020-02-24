# HES_633-2_ProgDist_VSFy

### 🚧 Work in progress 🚧

#### Students
David Crittin & Sylvain Meyer

#### Professor
Antoine Widmer

#### Deadline
13.06.2020

## Introduction 
The goal of the project is to create VSFy, a peer to peer audio streaming application with a synchronization server 

## Applications description: 
The server and client applications should be coded in java based on the libraries discovered during the module lessons. Keep it simple. The system will stream audio from one client to another and the audio file that can be streamed are synchronized on the server. 

## Work to do
### Minimal specifications of the server: 
- The server must be able to: 
  - [ ] Register new clients 
  - [ ] Maintain a list of registered clients (and the files they can stream) 
  - [ ] Give back a client IP and its list of audio file to another client
  - [ ] Accept multiple clients simultaneously (use threads). 
- The server must be able to write logs:
  - [ ] On a file 
  - [ ] The history must be kept (one file per month)
  - [ ] 3 levels of log (info, warning, severe) should be handled 
     - Info for all the useful operations
    - Warning for all the possible network errors
    - Severe for the exceptions
You can use command words to discuss between the client and the server or use different ports on the server for the client communications 

### Minimal specifications of the client: 
 - [ ] The client will be able to connect to the server through socket connections 
 - [ ] The client should be able to give its list of file to the server 
 - [ ] The client should be able to give its IP address 
 - [ ] The client should be able to get a list of clients with their available audio files 
 - [ ] The client should be able to ask for another client IP address 
 - [ ] The client should be able to connect to another client and ask to stream one file 
 - [ ] The client should be able to accept a network connection from another client and stream the selected file
 - [ ] The client should be able to play the audio stream  
 
 ### Possible add-ons
 The features described hereafter are examples of possible add-on that could be developed according to the progress of your project: 
- A client can handle stream audio files to multiple clients simultaneously 
- A client can handle video files as well
- Your imagination (must be related to java socket)

