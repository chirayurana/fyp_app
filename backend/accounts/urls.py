from django.urls import include , path

from rest_framework.routers import DefaultRouter 
from rest_framework.authtoken.views import ObtainAuthToken

from accounts import views

router = DefaultRouter()
router.register(r'user' , views.UserViewApi)


urlpatterns = [
    path('' , include(router.urls)),
    path('create_user' , views.UserCreateApi.as_view()),
    path('get_token' , ObtainAuthToken.as_view()),
    path('delete_token' , views.DeleteTokenAPI)
]
