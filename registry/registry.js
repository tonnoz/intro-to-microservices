import express from "express"
import fetch from 'node-fetch'

const app = express()
const registry = []

const printWelcomeMessage = (name, port) => {
    const jsonReg = JSON.stringify(registry)
    return `Registered service ${name} at port ${port} - registry: ${jsonReg}`
}

app.post("/:name/:port", (request, response) => {  
    const name = request.params.name
    const port = request.params.port
    let ports = []
    let welcomeMessage
    const filteredRegistry = registry.find(app => app.name === name)

    if (!filteredRegistry) {
        registry.push({
            name,
            ports: [port],
        })
        ports = [port]
        welcomeMessage = printWelcomeMessage(name, port)
    } else if (filteredRegistry.ports.indexOf(port) === -1) {
        ports = filteredRegistry.ports
        ports.push(port)
        welcomeMessage = printWelcomeMessage(name, port)
    }

    if (welcomeMessage) {
        console.log(welcomeMessage)
    }

    response.status(201);
    response.json({
        "name": name,
        "ports": ports
    })
})

app.get("/:name", (request, response) => {  
    const name = request.params.name
    const result = registry.find(app => app.name === name)

    if (!result || result.ports === undefined || result.ports.length === 0) {
        response.status(404)
        response.json({
            "name": name,
            "error": `Service ${name} not found!`
        })
        return
    }

    // TODO: Keep a counter somewhere or any other mechanism to return a
    // different port form the list. The roundrobin stategy should fit our
    // example.
    response.json(result)
})

app.get("/", (request, response) => {
    response.json(registry)
})

app.listen(3000, () => {                       
    console.log("Registry service started on port 3000")
})

const check = (item) => {
    const ports = item.ports

    return new Promise((resolve, reject) => {
        for (const port of ports) {
            const url = `http://localhost:${port}/ping`
            fetch(url)
            .catch((error) => {
                if (error.code === 'ECONNREFUSED') {
                    // Oh damn this port is down, reject it.
                    reject(port)
                }
            })
            .finally(() => {
                resolve()
            })
        }
    })
}

// Check every 5sec the
setInterval(() => {
    for (const item of registry) {
        if (item.ports.length > 0) {
            check(item).catch((rejectedPort) => {
                console.log("Service '" + item.name + "' at port " + rejectedPort + " is NOT working - unregistering it!!!")
                item.ports.splice(item.ports.indexOf(rejectedPort), 1)
            }
        )}
    }
}, 5000)
