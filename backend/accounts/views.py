from django.shortcuts import render

from rest_framework.viewsets import ModelViewSet , ReadOnlyModelViewSet
from rest_framework import generics
from rest_framework import permissions
from rest_framework.decorators import authentication_classes , permission_classes , api_view
from rest_framework import authentication
from rest_framework import permissions
from rest_framework.response import Response
from rest_framework import status 

from .models import CustomUser
from .serializers import UserViewSerializer , UserCreateSerializer


class UserViewApi(ReadOnlyModelViewSet):
  """
    Returns a list of all user
  """
  queryset = CustomUser.objects.all()
  serializer_class = UserViewSerializer


class UserCreateApi(generics.CreateAPIView):
  """
   For Creating New user
  """
  permission_classes = [permissions.AllowAny,]
  serializer_class = UserCreateSerializer


@api_view(["POST",])
@authentication_classes([authentication.TokenAuthentication,])
@permission_classes([permissions.IsAuthenticated])
def DeleteTokenAPI(request):
  """
   Deletes Auth token for user
  """
  if request.method == "POST":
    try : 
     request.user.auth_token.delete()
     return Response({"message": "User logged out sucessfully"} , status = status.HTTP_200_OK)
    except Exception as e:
      return Response({"Error":str(e)} , status = status.HTTP_500_INTERNAL_SERVER_ERROR)