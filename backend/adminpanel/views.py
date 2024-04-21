from django.shortcuts import render , redirect
from django.contrib.auth import authenticate , login , logout
from django.contrib.auth.decorators import login_required
from accounts.models import CustomUser
from api.models import Income , Expense , Budget , Subscription
from random import randint

@login_required(login_url="/login")
def admin_panel_home_page_view(request):
  content = {
    "total_users" : CustomUser.objects.all().count(),
    "total_transactions" : Income.objects.all().count() + Expense.objects.all().count(),
    "total_feedback" : 0,
    "server_load" : randint(0,15),
    "latest_expenses" : Expense.objects.order_by('-added_at')[:8],
    "latest_incomes" : Income.objects.order_by('-added_at')[:8]
  }
  return render(request , "index.html" , content)


def users_page_view(request):
  if request.method == "DELETE":
    pass
  content = {
    "users" : CustomUser.objects.all()
  }
  return render(request , "users.html" , content)

def login_view(request):
   if request.method == "POST":
        username = request.POST.get('username')
        password = request.POST.get('password')
        user = authenticate(username = username , password = password)
        if user is not None and user.is_staff:
          login(request , user)
          return redirect('/')
   return render(request , 'login.html')


def logout_view(request):
  logout(request)
  return redirect('/login')

def data_view(request):
  return render(request , 'data.html')

def expenses_view(request):
  return render(request , 'expenses.html' , {"expenses" : Expense.objects.all()})

def incomes_view(request):
  return render(request , 'incomes.html' , {"incomes":Income.objects.all()})


def budget_subscriptions_view(request):
  content = {
    "budgets" : Budget.objects.all(),
    "subscriptions" : Subscription.objects.all()
  }
  print(content)
  return render(request , "budget-subscriptions.html" , content)