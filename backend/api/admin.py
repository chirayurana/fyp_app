from django.contrib import admin
from api.models import CustomUser , Budget , Subscription , Income , Expense

admin.site.register(CustomUser)
admin.site.register(Budget)
admin.site.register(Subscription)
admin.site.register(Income)
admin.site.register(Expense)