from django.contrib import admin
from api.models import Budget , Subscription , Income , Expense
from accounts.models import CustomUser

admin.site.register(CustomUser)
admin.site.register(Budget)
admin.site.register(Subscription)
admin.site.register(Income)
admin.site.register(Expense)