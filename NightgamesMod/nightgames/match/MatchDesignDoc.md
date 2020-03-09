I'm just going to start with a running tally of all the things interacting here.

Match:
* main class
* holds participants
* holds areas (the map)

Area:
* Holds at most 1 encounter
* Has a list of whatever participants are there

Participant:
* tracks target validity
* holds a location

Encounter
* contains the run-up to combat
* and a handle to the combat for resuming after input