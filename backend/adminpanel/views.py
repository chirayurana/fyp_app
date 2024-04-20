from django.shortcuts import render , redirect
from django.contrib.auth import authenticate , login , logout
from django.contrib.auth.decorators import login_required
from accounts.models import CustomUser
from api.models import Income , Expense
from random import randint

@login_required(login_url="/login")
def admin_panel_home_page_view(request):
  number_of_users = CustomUser.objects.all().count()
  total_transactions = Income.objects.all().count() + Expense.objects.all().count()
  total_feedback = 0
  content = {
    "total_users" : CustomUser.objects.all().count(),
    "total_transactions" : Income.objects.all().count() + Expense.objects.all().count(),
    "total_feedback" : total_feedback,
    "server_load" : randint(0,15)
  }
  return render(request , "index.html" , content)


def users_page_view(request):
  return render(request , "users.html")

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