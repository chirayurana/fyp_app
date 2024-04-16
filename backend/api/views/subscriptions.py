from api.models import Subscription
from accounts.models import CustomUser

from rest_framework.viewsets import ModelViewSet , ReadOnlyModelViewSet
from rest_framework import generics , permissions , authentication , status , views
from rest_framework.decorators import authentication_classes , permission_classes
from rest_framework import serializers



class SubscriptionSerializer(serializers.ModelSerializer):
  owner = serializers.ReadOnlyField(source='owner.username')
  date = serializers.ReadOnlyField()
  last_paid = serializers.ReadOnlyField()

  class Meta:
    model = Subscription
    fields = ['owner','amount','name','renewal_after','last_paid','date']


class SubscriptionView(ModelViewSet):
  serializer_class = SubscriptionSerializer
  authentication_classes = [authentication.TokenAuthentication]
  permission_classes = [permissions.IsAuthenticated]
  queryset = Subscription.objects.all()

  def perform_create(self , serializer):
    serializer.save(owner=self.request.user)
  
  def get_query(self):
    return Subscription.objects.filter(owner=self.request.owner)