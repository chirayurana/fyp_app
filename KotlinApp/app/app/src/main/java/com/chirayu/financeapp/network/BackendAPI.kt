package com.chirayu.financeapp.network

import com.chirayu.financeapp.network.models.RemoteExpense
import com.chirayu.financeapp.network.models.RemoteBudget
import com.chirayu.financeapp.network.models.RemoteIncome
import com.chirayu.financeapp.network.models.RemoteSubscription
import com.chirayu.financeapp.network.models.Token
import com.chirayu.financeapp.network.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BackendAPI {

    @POST("create_user")
    suspend fun signup(@Body user : User) : Response<User>

    @POST("get_token")
    suspend fun login(@Body user: User) : Response<Token>

    @Headers("Authorization: token")
    @GET("users")
    suspend fun userDetails() : Response<List<User>>

    @Headers("Authorization: token")
    @POST("delete_token")
    suspend fun logout() : Response<Void>

    // Expenses endpoints
    @Headers("Authorization: token")
    @GET("expenses")
    suspend fun retrieveAllExpenses() : Response<List<RemoteExpense>>

    @Headers("Authorization: token")
    @POST("expenses/")
    suspend fun addExpense(@Body expense: RemoteExpense) : Response<RemoteExpense>

    @Headers("Authorization: token")
    @GET("expenses/{expense_id}")
    suspend fun retrieveExpense(@Path("expense_id") expenseId : Int) : Response<RemoteExpense>

    @Headers("Authorization: token")
    @PUT("expenses/{expense_id}/")
    suspend fun updateExpense(@Path("expense_id") expenseId : Int,@Body expense: RemoteExpense) : Response<RemoteExpense>

    @Headers("Authorization: token")
    @DELETE("expenses/{expense_id}/")
    suspend fun deleteExpense(@Path("expense_id") expenseId : Int) : Response<Void>

    // budgets endpoints
    @Headers("Authorization: token")
    @GET("budgets")
    suspend fun retrieveAllBudgets() : Response<List<RemoteBudget>>

    @Headers("Authorization: token")
    @POST("budgets/")
    suspend fun addBudget(@Body budget: RemoteBudget) : Response<RemoteBudget>

    @Headers("Authorization: token")
    @GET("budgets/{budget_id}")
    suspend fun retrieveBudget(@Path("budget_id") budgetId : Int) : Response<RemoteBudget>

    @Headers("Authorization: token")
    @PUT("budgets/{budget_id}/")
    suspend fun updateBudget(@Path("budget_id") budgetId : Int,@Body budget: RemoteBudget) : Response<RemoteBudget>

    @Headers("Authorization: token")
    @DELETE("budgets/{budget_id}/")
    suspend fun deleteBudget(@Path("budget_id") budgetId : Int) : Response<Void>

    // Subscription endpoints
    @Headers("Authorization: token")
    @GET("subscriptions")
    suspend fun retrieveAllSubscriptions() : Response<List<RemoteSubscription>>

    @Headers("Authorization: token")
    @POST("subscriptions/")
    suspend fun addSubscription(@Body subscription: RemoteSubscription) : Response<RemoteSubscription>

    @Headers("Authorization: token")
    @GET("subscriptions/{subscription_id}")
    suspend fun retrieveSubscription(@Path("subscription_id") subscriptionId : Int) : Response<RemoteSubscription>

    @Headers("Authorization: token")
    @PUT("subscriptions/{subscription_id}/")
    suspend fun updateSubscription(@Path("subscription_id") subscriptionId : Int,@Body subscription: RemoteSubscription) : Response<RemoteSubscription>

    @Headers("Authorization: token")
    @DELETE("subscriptions/{subscription_id}/")
    suspend fun deleteSubscription(@Path("subscription_id") subscriptionId : Int) : Response<Void>

    // Incomes endpoints
    @Headers("Authorization: token")
    @GET("incomes")
    suspend fun retrieveAllIncomes() : Response<List<RemoteIncome>>

    @Headers("Authorization: token")
    @POST("incomes/")
    suspend fun addIncome(@Body income: RemoteIncome) : Response<RemoteIncome>

    @Headers("Authorization: token")
    @GET("incomes/{income_id}")
    suspend fun retrieveIncome(@Path("income_id") incomeId : Int) : Response<RemoteIncome>

    @Headers("Authorization: token")
    @PUT("incomes/{income_id}/")
    suspend fun updateIncome(@Path("income_id") incomeId : Int,@Body income: RemoteIncome) : Response<RemoteIncome>

    @Headers("Authorization: token")
    @DELETE("incomes/{income_id}/")
    suspend fun deleteIncome(@Path("income_id") incomeId : Int) : Response<Void>
}