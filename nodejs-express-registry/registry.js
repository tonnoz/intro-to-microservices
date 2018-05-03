import express from "express"
import fetch from 'node-fetch'

const app = express()
const registry = []
const REGISTRY_POLLING_INTERVAL_MSEC = 5000; //interval in msec for checking that a service is still alive via polling its health endpoint
const REGISTRY_PORT = 3000; //port on which the registry is running


/** function that returns a welcome message for the given service name and port **/
const formatWelcomeMessage = (name, port) => {
    const jsonReg = JSON.stringify(registry)
    return `Registered service ${name} at port ${port} - registry: ${jsonReg}`
}




/** POST endpoint that allows a service to register itself by communicating its name and on
 * which port it is running **/
app.post("/:name/:port", (request, response) => {
    const name = request.params.name
    const port = request.params.port
    let ports = []
    let welcomeMessage
    const service = registry.find(app => app.name === name)
if (!service) {
    registry.push({
        name,
        ports: [port]
    })
    ports = [port]
    welcomeMessage = formatWelcomeMessage(name, port)
} else if (service.ports.indexOf(port) === -1) {
    ports = service.ports
    ports.push(port)
    welcomeMessage = formatWelcomeMessage(name, port)
}
console.log(welcomeMessage)
response.status(201);
response.json({
    "name": name,
    "ports": ports
})
})

/** GET endpoint that returns a good candidate port on which the requested service is running.
 * TODO: implement a better algorithm to choose the best available port **/
app.get("/:name", (request, response) => {
    console.log(`received request of discovery for  ${request.params.name} `)

const name = request.params.name
const service = registry.find(app => app.name === name)
console.log(`the service ${request.params.name} is running at port(s): ${service.ports} `)

if (!service || !service.ports || service.ports.length === 0) {
    response.status(404)
    response.json({
        "name": name,
        "error": `Service ${name} not found!`
    })
    return
}
response.json(service.ports[0])
})

/** GET endpoint exposed by the registry: return the whole registry object json **/
app.get("/", (request, response) => {
    response.json(registry)
})

/** Booting of Express app **/
app.listen(REGISTRY_PORT, () => {
    console.log("Registry service started on port 3000")
})

/** function that ping the service "service" to check if it is alive and return a Promise for it **/
const check = (service) => {
    const ports = service.ports
    return new Promise((resolve, reject) => {
        for(const port of ports){
        const PING_URL = `http://localhost:${port}/ping`
        fetch(PING_URL)
            .catch((error) => {
            if(error.code === 'ECONNREFUSED'){ //port not available
            reject(port)
        }
    }).finally(() => {
            resolve()
        })
    }
})
}


/** Every REGISTRY_POLLING_INTERVAL_MSEC milliseconds check if all the registered services are
 * still functioning by pinging them, if they are unresponsive, removes them from the available
 * port list **/
setInterval(() => {
    for (const item of registry) {
    if (item.ports.length > 0) {
        check(item).catch((rejectedPort) => {
            console.log("Service '" + item.name + "' at port " + rejectedPort + " is NOT working - unregistering it!")
        item.ports.splice(item.ports.indexOf(rejectedPort), 1)
    }
    )}
}
},REGISTRY_POLLING_INTERVAL_MSEC)
