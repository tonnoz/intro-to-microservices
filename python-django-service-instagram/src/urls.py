from django.conf.urls import url
from django.contrib import admin

from src.views import get_ping, on_callback, request_token, search_tags

urlpatterns = [
    url(r'^admin/', admin.site.urls),
    url(r'request_token', request_token),
    url(r'oauth_callback', on_callback),
    url(r'tag', search_tags),
    url(r'ping', get_ping),
]
