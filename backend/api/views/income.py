from django.shortcuts import render
from api.models import Income
from accounts.models import CustomUser

from rest_framework.viewsets import ModelViewSet , ReadOnlyModelViewSet
from rest_framework import generics , permissions , authentication , status , views
from rest_framework.decorators import authentication_classes , permission_classes
from rest_framework import serializers

class IncomeSerializer(serializers.ModelSerializer):
  owner = serializers.ReadOnlyField(source='owner.username')
  date = serializers.ReadOnlyField(source='added_at')
  class Meta:
    model = Income
    fields = ['amount' , 'description' , 'income_type' , 'owner', 'date']
  

class IncomeView(ModelViewSet):
  serializer_class = IncomeSerializer
  authentication_classes = [authentication.TokenAuthentication]
  permission_classes = [permissions.IsAuthenticated]
  queryset = Income.objects.all()

  def perform_create(self , serializer):
    serializer.save(owner=self.request.user)
  
  def get_queryset(self):
    return Income.objects.filter(owner=self.request.user)
