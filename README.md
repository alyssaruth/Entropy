# Entropy

Client/Server application for the card game Entropy.

## Migration plan

This project has recently been revived with a plan to modernise it & use it to play with some technologies that I've not got the chance to work with yet.
There are currently three planned "phases" in mind, outlined below.

All work is tracked here: https://trello.com/b/bO4uMw5e/entropy

### Phase I - Clean-up & prepare for stateless

Essentially the "tidy-up" phase, the bulk of this work is converting to a modern REST-based API. 

In detail:

- ~~Refactor project structure and use gradle to build/manage dependencies/etc~~
- Replace all the XML-based client/server comms with new endpoints built with KTOR/jackson
- Ditch all the hand-cranked crypto stuff.
- Replace NotificationSocket with simpler impl (KTOR SSE)
- ~~Ditch Proguard~~
- ~~Remove the concept of "accounts" - so rather than sign up/log in, allow people to just jump online with a username~~
- ~~Remove all the email stuff (no longer needed without accounts)~~
- Get the client into a releasable state - finish WIP achievements from when I left off years ago.
- Replace old logging mechanism with `Logger`
- ~~Introduce Store abstraction in the backend code - just an "in-memory" implementation for now~~
- Update all server-side state to use Store abstraction

Once this phase is complete, the API (and therefore client code) shouldn't need to be changed in the remaining phases.

### Phase II - Stateless backend

The goal of this phase is to make the backend clustered - so we should be able to run multiple instances in e.g. ECS.

Two main strands to this:

 - All server-side "state" is externalised (implemented with DynamoDB)
 - Replace web sockets/server side events  (https://docs.aws.amazon.com/apigateway/latest/developerguide/apigateway-websocket-api.html)

I also want to use Pulumi to provision/test the new infrastructure required for this. 

### Phase III - Serverless

With all the state externalised, we can go serverless. Implementation will be AWS lambda.
I have various open questions to explore once we get here:

 - Is it feasible to stick with Kotlin/JVM as the implementation, or too slow?
 - If not, what instead? KotlinJS / Kotlin Native / NodeJS / something else?
 - Do we keep KTOR as a "front door" for local development, or ditch this entirely? What's the local dev story with lambda?

## Develop locally

Currently there's no infrastructure, so building & running the project is just some gradle tasks.

To run the client/server:

```bash
./gradlew :client:run # Run the client
./gradlew :server:run # Run the server
```

Other tasks:

```bash
./gradlew test        # Run tests     
./gradlew ktfmtCheck  # Check formatting
./gradlew ktfmtFormat # Reformat all files
```
