from django.db import models
from django.contrib.auth.models import AbstractUser

# Create your models here.
class CustomUser(AbstractUser):
    total_balance = models.FloatField(default=0)
    number_of_transactions = models.IntegerField(default=0)
    number_of_budgets = models.IntegerField(default=0)
    number_of_subscriptions = models.IntegerField(default=0)

    def __str__(self):
        return f"{self.username} - {self.total_balance}"