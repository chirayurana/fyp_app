# Generated by Django 5.0.4 on 2024-04-16 04:10

import django.db.models.deletion
from django.conf import settings
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Transaction',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('amount', models.FloatField()),
                ('description', models.CharField(max_length=300, null=True)),
                ('added_at', models.DateTimeField(auto_now_add=True)),
                ('owner', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.CreateModel(
            name='Budget',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=100)),
                ('max_limit', models.IntegerField()),
                ('current_spent', models.IntegerField(default=0)),
                ('added_at', models.DateField(auto_now_add=True)),
                ('expiry_at', models.DateField()),
                ('owner', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Income',
            fields=[
                ('transaction_ptr', models.OneToOneField(auto_created=True, on_delete=django.db.models.deletion.CASCADE, parent_link=True, primary_key=True, serialize=False, to='api.transaction')),
                ('income_type', models.CharField(choices=[('Salary', 'Salary'), ('Bonus', 'Bonus'), ('Other', 'Other')], max_length=7)),
            ],
            bases=('api.transaction',),
        ),
        migrations.CreateModel(
            name='Subscription',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=100)),
                ('max_limit', models.IntegerField()),
                ('current_spent', models.IntegerField(default=0)),
                ('added_at', models.DateField(auto_now_add=True)),
                ('expiry_at', models.DateField()),
                ('last_paid', models.DateField(auto_now_add=True)),
                ('renewal_after', models.IntegerField(default=30)),
                ('owner', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Expense',
            fields=[
                ('transaction_ptr', models.OneToOneField(auto_created=True, on_delete=django.db.models.deletion.CASCADE, parent_link=True, primary_key=True, serialize=False, to='api.transaction')),
                ('expense_type', models.CharField(choices=[('Shopping', 'Shopping'), ('Medical', 'Medical'), ('Transportation', 'Transportation'), ('Other', 'Other')], max_length=15)),
                ('budget', models.ForeignKey(default=None, on_delete=django.db.models.deletion.CASCADE, to='api.budget')),
            ],
            bases=('api.transaction',),
        ),
    ]