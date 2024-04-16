from django.urls import include , path

from rest_framework.routers import DefaultRouter 
from rest_framework.authtoken.views import ObtainAuthToken

from accounts import views
from api.views.transaction import ExpenseView

router = DefaultRouter()
router.register(r'users' , views.UserViewApi , basename="user")
#router.register(r'expenses' , ExpenseView)


urlpatterns = [
    path('' , include(router.urls)),
    path('create_user' , views.UserCreateApi.as_view()),
    path('get_token' , ObtainAuthToken.as_view()),
    path('delete_token' , views.DeleteTokenAPI)
]
