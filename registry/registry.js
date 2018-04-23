import express from "express"
import fetch from 'node-fetch'

const app = express()
const registry = {}

app.post("/:name/:port", (request, response) => {  
    const name = request.params.name
    const port = request.params.port

    const ports = registry[name]
    if (ports === undefined) {
        ports = []
    }

    if (ports.indexOf(port) == -1) {
        ports.push(port)
        registry[name] = ports
        console.log(`Registered service ${name} at port ${port} - registry: ${JSON.stringify(registry)}`)
    }

    response.status(201);
    response.json({
        "name": name,
        "ports": ports
    });
});

app.get("/:name", (request, response) => {  
    const name = request.params.name
    const ports = registry[name]
    if (ports === undefined) {
        response.status(404)
        response.json({
            "name": name,
            "error": `Service ${name} not found!`
        })
        return
    }   

    response.json({
        "name": name,
        "port": ports[0]
    })
})

app.get("/", (request, response) => {  
    text = JSON.stringify(registry)
    response.write(text)
    response.end()
})

app.listen(3000, () => {                       
    console.log("Registry service started on port 3000")
})

setInterval(() => {
    const check = (name, port) => {
        const url = "http://localhost:" + port + "/ping"
        fetch(url).then(function(res) {
            if (res.error) {
                registry[name] = ports.splice(index, 1)
                console.log("Service '" + name + "' at port " + port + " is NOT working - unregistering it!!!")
            }
        })
    }

    for (let name in registry) {
        const ports = registry[name]
        for (let index = 0; index < ports.length; index++) {
            check(name, ports[index])
        }
    }
}, 10000)