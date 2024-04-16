from django.urls import path , include

from rest_framework import routers

from .views.budget import BudgtView

router = routers.DefaultRouter()
router.register(r'budgets' , BudgtView)

urlpatterns = [
    path('' , include(router.urls))
]

