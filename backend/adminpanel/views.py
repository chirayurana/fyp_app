from django.shortcuts import render




def admin_panel_home_page_view(request):
  return render(request , "index.html")