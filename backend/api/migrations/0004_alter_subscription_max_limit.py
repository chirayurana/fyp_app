# Generated by Django 5.0.4 on 2024-04-16 07:02

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0003_subscription_active_subscription_amount'),
    ]

    operations = [
        migrations.AlterField(
            model_name='subscription',
            name='max_limit',
            field=models.FloatField(null=True),
        ),
    ]
