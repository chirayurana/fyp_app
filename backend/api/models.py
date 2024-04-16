from django.db import models
from accounts.models import CustomUser



class BaseBudget(models.Model):
    owner = models.ForeignKey(CustomUser, on_delete=models.CASCADE)
    name = models.CharField(max_length=100)
    max_limit = models.IntegerField()
    current_spent = models.IntegerField()
    added_at = models.DateField(auto_now_add=True)
    expiry_at = models.DateField()

    class Meta:
        abstract = True
    
    def __str__(self):
        return f"{self.owner} - {self.name} - {self.max_limit} - {self.current_spent}"

class Budget(BaseBudget):
    pass

class Subscription(BaseBudget):
    last_paid = models.DateField(auto_now_add=True)
    renewal_after = models.IntegerField(default=30)

    def __str__(self):
        return f"{self.owner} - {self.name}"

class Transaction(models.Model):
    owner = models.ForeignKey(CustomUser, on_delete=models.CASCADE)
    amount = models.FloatField()
    description = models.CharField(max_length=300, null=True)
    added_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.owner} - {self.amount} - {self.description}"

class Income(Transaction):
    INCOME_TYPES = (
        ('Salary', 'Salary'),
        ('Bonus', 'Bonus'),
        ('Other', 'Other'),
    )

    income_type = models.CharField(max_length=7, choices=INCOME_TYPES)

class Expense(Transaction):
    budget = models.ForeignKey(Budget, on_delete=models.CASCADE , default=None)
    EXPENSE_TYPES = (
        ('Shopping', 'Shopping'),
        ('Medical', 'Medical'),
        ('Transportation', 'Transportation'),
        ('Other', 'Other'),
    )

    expense_type = models.CharField(max_length=15, choices=EXPENSE_TYPES)

    def __str__(self):
        return f"{self.owner} - {self.amount} - {self.description} - {self.expense_type}"
