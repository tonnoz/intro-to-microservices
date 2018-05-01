
# Python microService

This webservice feches data from Instagram API.

exposed endpoints:
```
[GET] /tag?q=YOUR_TAG
[GET] /request_token
```

You may need to first call the request_token endpoint the first time, this will
get a token from Instagram then cache it.


### Requires

Python3.6, pip 10.0.1

### Installation

```sh
make clean
make setup
```

Set the client_id and client_secret in src/views.py

```py
CONFIG = {
    'client_id': 'YOUT_CLIENT_ID',
    'client_secret': 'YOUR_CLIENT_SECRET',
}
```

You can get your keys at:

https://www.instagram.com/developer/

You will need to create a new app.

### Running

```sh
make run PORT=YOUR_PORT
````

If not specified, port 8000 will be used.