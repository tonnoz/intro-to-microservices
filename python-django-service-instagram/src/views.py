from django.core.cache import cache
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from threading import Thread
import json
import requests
import sys
import time

APP_PORT = sys.argv[-1]

CONFIG = {
    'client_id': 'YOUT_CLIENT_ID',
    'client_secret': 'YOUR_CLIENT_SECRET',
    'redirect_uri': 'http://localhost:%s/oauth_callback' % APP_PORT
}

def exchange_code_for_access_token(code):
    url = 'https://api.instagram.com/oauth/access_token'
    data = {
        'client_id': CONFIG['client_id'],
        'client_secret': CONFIG['client_secret'],
        'code': code,
        'grant_type': 'authorization_code',
        'redirect_uri': CONFIG['redirect_uri']
    }

    response = requests.post(url, data=data)
    account_data = json.loads(response.content)

    if account_data['access_token']:
        return account_data['access_token']
    return None


@api_view(['GET'])
def request_token(request):
    payload = {
        'client_id': CONFIG['client_id'],
        'redirect_uri': CONFIG['redirect_uri'],
        'response_type': 'code',
        'scope': ["public_content"],
    }
    url = 'https://api.instagram.com/oauth/authorize/'

    req = requests.get(url, params=payload)
    return Response(req.url, status=status.HTTP_200_OK)


@api_view(['GET'])
def on_callback(request):
    code = request.GET.get("code")
    if not code:
        return Response('Missing code', status=status.HTTP_400_BAD_REQUEST)
    try:
        access_token = exchange_code_for_access_token(code)
        if not access_token:
            return Response(
                'Could not get access token',
                status=status.HTTP_400_BAD_REQUEST)

        # Cache the access_token in memory.
        cache.set('access_token', access_token, None)
    except Exception as e:
        print(e)
    return Response(code, status=status.HTTP_202_ACCEPTED)


@api_view(['GET'])
def search_tags(request):
    tag = request.GET.get("q")
    if not tag:
        return Response('Missing tag', status=status.HTTP_400_BAD_REQUEST)

    if cache.get('access_token'):
        access_token = cache.get('access_token')
        url = u'https://api.instagram.com/v1/tags/search'
        payload = {
            'access_token': access_token,
            'q': tag,
        }

        res = requests.get(url, params=payload)
        return Response(json.loads(res.text), status=status.HTTP_200_OK)
    else:
        msg = 'Please call /request_token before to call this endpoint.'
        return Response(msg, status=status.HTTP_403_FORBIDDEN)


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
        url = 'http://localhost:3000/django/%s' % APP_PORT
        try:
            requests.post(url, params={})
        except:
            print('Registry not reachable, try again in 3sec')
        time.sleep(3)

thread = Thread(name='process_health_checker', target=say_hello)
thread.daemon = True
thread.start()
