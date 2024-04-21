from django.db import models
from accounts.models import CustomUser

class BaseBudget(models.Model):
    owner = models.ForeignKey(CustomUser, on_delete=models.CASCADE)
    name = models.CharField(max_length=100)
    max_limit = models.IntegerField()
    current_spent = models.IntegerField(default=0)
    added_at = models.DateField(auto_now_add=True)
    expiry_at = models.DateField(null=True , blank=True)

    class Meta:
        abstract = True
    
    def __str__(self):
        return f"{self.owner} - {self.name} - {self.max_limit} - {self.current_spent}"
    
    def dict(self):
        return {
            'owner': self.owner.username,
            'name': self.name,
            'max_limit': self.max_limit,
            'current_spent': self.current_spent,
            'added_at': self.added_at,
            'expiry_at': self.expiry_at
        }

    def update_current_spent(self, amount):
        self.current_spent += amount
        self.save()

class Budget(BaseBudget):
    def dict(self):
        base_dict = super().dict()
        return base_dict
    
    def save(self, *args, **kwargs):
       self.owner.number_of_budgets += 1
       self.owner.save()
       super().save(*args, **kwargs)


class Subscription(BaseBudget):
    max_limit = models.FloatField(null=True)
    amount = models.FloatField(null=True)
    active = models.BooleanField(default=True )
    last_paid = models.DateField(auto_now_add=True)
    renewal_after = models.IntegerField(default=30)

    def __str__(self):
        return f"{self.owner} - {self.name}"

    def dict(self):
        base_dict = super().dict()
        base_dict.update({
            'last_paid': self.last_paid,
            'renewal_after': self.renewal_after
        })
        return base_dict
    
    
    def save(self, *args, **kwargs):
       self.owner.number_of_subscriptions += 1
       self.owner.save()
       super().save(*args, **kwargs)

class Transaction(models.Model):
    owner = models.ForeignKey(CustomUser, on_delete=models.CASCADE)
    amount = models.FloatField()
    description = models.CharField(max_length=300, null=True)
    added_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.owner} - {self.amount} - {self.description}"
    
    def dict(self):
        return {
            'owner': self.owner,
            'amount': self.amount,
            'description': self.description,
            'added_at': self.added_at
        }


class Income(Transaction):
    INCOME_TYPES = (
        ('Salary', 'Salary'),
        ('Bonus', 'Bonus'),
        ('Other', 'Other'),
    )

    income_type = models.CharField(max_length=7, choices=INCOME_TYPES)

    def save(self, *args, **kwargs):
        self.owner.total_balance += self.amount
        self.owner.number_of_transactions += 1
        self.owner.save()
        super().save(*args, **kwargs)
class Expense(Transaction):
    budget = models.ForeignKey(Budget, on_delete=models.CASCADE , blank=True , null=True)
    EXPENSE_TYPES = (
        ('Shopping', 'Shopping'),
        ('Medical', 'Medical'),
        ('Transportation', 'Transportation'),
        ('Other', 'Other'),
    )

    expense_type = models.CharField(max_length=15, choices=EXPENSE_TYPES)


    def save(self, *args, **kwargs):
       if self.budget:
        self.budget.update_current_spent(self.amount)
       self.owner.total_balance -= self.amount
       self.owner.number_of_transactions += 1
       self.owner.save()
       super().save(*args, **kwargs)


    def __str__(self):
        return f"{self.owner} - {self.amount} - {self.description} - {self.expense_type}"
    
    def dict(self):
        base_dict = super().dict()
        base_dict.update({
            'budget': self.budget,
            'expense_type': self.expense_type
        })
        return base_dict

