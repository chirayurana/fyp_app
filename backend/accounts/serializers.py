from rest_framework import serializers
from rest_framework.validators import UniqueValidator
from django.contrib.auth.password_validation import validate_password
from rest_framework.response import Response
from datetime import date   

from .models import CustomUser

class UserViewSerializer(serializers.ModelSerializer):
  class Meta:
    model = CustomUser
    fields = ['username' , 'date_joined' , 'total_balance',]

class UserCreateSerializer(serializers.ModelSerializer):

  confirm_password = serializers.CharField(write_only=True)

  class Meta:
    model = CustomUser
    fields = ['username' , 'total_balance' , 'password' , 'confirm_password']
    extra_kwargs =  {'password': {'write_only' : True}}
  
  def validate(self , data):
    if data['password'] != data['confirm_password']:
      raise serializers.ValidationError({"Password" : "Your Passwords did not Match"})
    return data

  
  def create(self , valid_data):
    user = CustomUser.objects.create(
      username= valid_data['username'],
      total_balance= valid_data['total_balance']
    )
    user.set_password(valid_data['password'])
    user.save()
    return user