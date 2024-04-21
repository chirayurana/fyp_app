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
    "database_status" : "Connected",
    "latest_expenses" : Expense.objects.order_by('-added_at')[:8],
    "latest_incomes" : Income.objects.order_by('-added_at')[:8]
  }
  return render(request , "index.html" , content)

@login_required(login_url="/login")
def users_page_view(request):
  if request.method == "POST":
    method = request.POST.get('method')
    if method == "DELETE":
      user_id = request.POST.get('user_id')
      target_user = CustomUser.objects.get(id=int(user_id))
      target_user.delete()
    elif method == "POST":
      search_keyword = request.POST.get('search_keyword')
      content = {
        "users" : CustomUser.objects.all().filter(username__icontains=search_keyword)
      }
      return render(request , "users.html" , content)
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
  content = {"expenses" : Expense.objects.all()}
  if request.method == 'POST':
    search_keyword = request.POST.get('search_keyword')
    content = {
      "expenses" : Expense.objects.all().filter(expense_type__icontains=search_keyword)
    }
  return render(request , 'expenses.html' , content)

def incomes_view(request):
  content = {"incomes" :Income.objects.all()}
  if request.method == 'POST':
    search_keyword = request.POST.get('search_keyword')
    content = {
      "incomes" : Income.objects.all().filter(income_type__icontains=search_keyword)
    }
    print(content)
  return render(request , 'incomes.html' , content)


def budget_subscriptions_view(request):
  content = {
    "budgets" : Budget.objects.all(),
    "subscriptions" : Subscription.objects.all()
  }
  if request.method == 'POST':
    search_keyword = request.POST.get('search_keyword')
    content = {
      "budgets" : Budget.objects.all().filter(name__icontains=search_keyword),
      "subscriptions" : Subscription.objects.all().filter(name__icontains=search_keyword)
    }
  return render(request , "budget-subscriptions.html" , content)