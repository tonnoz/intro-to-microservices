from django.conf.urls import url
from django.contrib import admin

from src.views import get_ping

urlpatterns = [
    url(r'^admin/', admin.site.urls),
    url(r'ping', get_ping),
]
