from django.shortcuts import render
from api.models import Budget

from rest_framework.viewsets import ModelViewSet , ReadOnlyModelViewSet
from rest_framework import generics , permissions , authentication , status , views
from rest_framework.decorators import authentication_classes , permission_classes
from rest_framework import serializers

class BudgetSerializer(serializers.ModelSerializer):
  owner = serializers.ReadOnlyField(source='owner.username')
  date = serializers.ReadOnlyField(source='added_at')
  class Meta:
    model = Budget
    fields = ['id','name','max_limit','current_spent','expiry_at', 'owner' , 'date' ]


class BudgetView(ModelViewSet):
  serializer_class = BudgetSerializer
  authentication_classes = [authentication.TokenAuthentication]
  permission_classes = [permissions.IsAuthenticated]
  queryset = Budget.objects.all()

  def perform_create(self , serializer):
    serializer.save(owner=self.request.user)
  
  def get_queryset(self):
    return Budget.objects.filter(owner=self.request.user)