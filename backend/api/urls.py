from django.urls import path , include

from rest_framework import routers

from .views.budget import BudgetView
from  .views.income import IncomeView
from .views.expense import ExpenseView
from .views.subscriptions import SubscriptionView

router = routers.DefaultRouter()
router.register(r'budgets' , BudgetView , basename="budget")
router.register(r'incomes' , IncomeView , basename="income")
router.register(r'expenses' , ExpenseView , basename="expense")
router.register(r'subscriptions' , SubscriptionView , basename="subscription")

urlpatterns = [
    path('' , include(router.urls))
]

