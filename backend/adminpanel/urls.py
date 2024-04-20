from .views import admin_panel_home_page_view , users_page_view , login_view , logout_view , data_view , incomes_view , expenses_view
from django.urls import path

urlpatterns = [
    path('' , admin_panel_home_page_view , name="admin-panel"),
    path('users' , users_page_view , name="users"),
    path('login' , login_view , name="login"),
    path('logout' , logout_view , name="logout"),
    path('data' , data_view , name="data"),
    path('incomes' , incomes_view , name="incomes"),
    path('expenses' , expenses_view , name="expenses")
]
