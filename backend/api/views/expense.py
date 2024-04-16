from django.shortcuts import render
from api.models import Expense
from accounts.models import CustomUser

from rest_framework.viewsets import ModelViewSet , ReadOnlyModelViewSet
from rest_framework import generics , permissions , authentication , status , views
from rest_framework.decorators import authentication_classes , permission_classes
from rest_framework import serializers


class ExpenseSerializer(serializers.ModelSerializer):
  owner = serializers.ReadOnlyField(source='owner.username')
  date = serializers.ReadOnlyField(source='added_at')
  class Meta:
    model = Expense
    fields = ['amount' , 'description' , 'budget' , 'expense_type' , 'owner', 'date']
  

class ExpenseView(ModelViewSet):
  serializer_class = ExpenseSerializer
  authentication_classes = [authentication.TokenAuthentication]
  permission_classes = [permissions.IsAuthenticated]
  queryset = Expense.objects.all()

  def perform_create(self , serializer):
    serializer.save(owner=self.request.user)
  
  def get_queryset(self):
    return Expense.objects.filter(owner=self.request.user)

