from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from threading import Thread
import requests
import sys
import time

@api_view(['GET'])
def get_ping(request):
    """
    Public endpoint responding to a ping with pong! 
    """
    print("ping received :)")
    return Response("pong", status=status.HTTP_202_ACCEPTED)

def say_hello():
    """
    Say Hello to the registry server. This function is called every 3sec.
    """
    while True:
        url = 'http://localhost:3000/django/%s' % sys.argv[-1] 
        try:
            requests.post(url, params={})
        except:
            print('Registry not reachable, try again in 3sec')
        time.sleep(3)

thread = Thread(name='process_health_checker', target=say_hello)
thread.daemon = True
thread.start()
