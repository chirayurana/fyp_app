from .views import admin_panel_home_page_view , users_page_view , login_view
from django.urls import path

urlpatterns = [
    path('' , admin_panel_home_page_view , name="admin-panel"),
    path('users' , users_page_view , name="users"),
    path('login' , login_view , name="login")
]
