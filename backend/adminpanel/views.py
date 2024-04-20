from django.shortcuts import render




def admin_panel_home_page_view(request):
  return render(request , "index.html")


def users_page_view(request):
  return render(request , "users.html")

def login_view(request):
  if request.method == 'POST':
    print(request.POST)
    print(request.POST.get('username'))
    print(request.POST.get('password'))
  return render(request , 'login.html')