from django.shortcuts import render , redirect
from django.contrib.auth import authenticate , login , logout
from django.contrib.auth.decorators import login_required

@login_required(login_url="/login")
def admin_panel_home_page_view(request):
  return render(request , "index.html")


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