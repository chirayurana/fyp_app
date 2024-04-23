package com.chirayu.financeapp.util

import com.chirayu.financeapp.MainActivity
import com.chirayu.financeapp.R
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.model.entities.Budget
import com.chirayu.financeapp.model.entities.Movement
import com.chirayu.financeapp.model.entities.Subscription
import com.chirayu.financeapp.model.entities.mapToRemoteBudget
import com.chirayu.financeapp.model.entities.mapToRemoteExpense
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.models.RemoteSubscription
import com.chirayu.financeapp.network.models.mapToBudget
import com.chirayu.financeapp.network.models.mapToMovement
import com.chirayu.financeapp.network.models.mapToTaggedMovement
import com.chirayu.financeapp.util.StringUtil.toBudgetOrNull
import com.chirayu.financeapp.util.StringUtil.toMovementOrNull
import com.chirayu.financeapp.util.StringUtil.toRemoteSubscriptionOrNull
import com.chirayu.financeapp.util.StringUtil.toSubscriptionOrNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate

object ImportExportUtil {
    const val CREATE_MOVEMENTS_FILE: Int = 2
    const val OPEN_MOVEMENTS_FILE: Int = 3
    const val CREATE_MOVEMENTS_TEMPLATE = 5

    const val CREATE_SUBSCRIPTIONS_FILE: Int = 7
    const val OPEN_SUBSCRIPTIONS_FILE: Int = 11
    const val CREATE_SUBSCRIPTIONS_TEMPLATE: Int = 13

    const val CREATE_BUDGETS_FILE: Int = 17
    const val OPEN_BUDGETS_FILE: Int = 19
    const val CREATE_BUDGETS_TEMPLATE = 23

    fun createExportFile(dataType: Int, activity: MainActivity) {
        val today = LocalDate.now().toString()
        when (dataType) {
            CREATE_MOVEMENTS_FILE -> {
                activity.exportMovements.launch(
                    String.format(
                        activity.getString(R.string.movements_file_name),
                        today
                    )
                )
            }

            CREATE_SUBSCRIPTIONS_FILE -> {
                activity.exportSubscriptions.launch(
                    String.format(
                        activity.getString(R.string.subscriptions_file_name),
                        today
                    )
                )
            }

            CREATE_BUDGETS_FILE -> {
                activity.exportBudgets.launch(
                    String.format(
                        activity.getString(R.string.budgets_file_name),
                        today
                    )
                )
            }

            CREATE_MOVEMENTS_TEMPLATE -> {
                activity.createMovementsTemplate.launch(
                    activity.getString(R.string.movements_template)
                )
            }

            CREATE_SUBSCRIPTIONS_TEMPLATE -> {
                activity.createSubscriptionsTemplate.launch(
                    activity.getString(R.string.subscriptions_template)
                )
            }

            else -> {
                activity.createBudgetsTemplate.launch(
                    activity.getString(R.string.budgets_template)
                )
            }
        }
    }

    fun export(type: Int, outputStream: FileOutputStream, app: SaveAppApplication) {
        val writer = outputStream.bufferedWriter()
        when (type) {
            CREATE_MOVEMENTS_FILE -> exportMovements(writer, app)
            CREATE_SUBSCRIPTIONS_FILE -> exportSubscriptions(writer, app)
            CREATE_BUDGETS_FILE -> exportBudgets(writer, app)
        }

        writer.flush()
        outputStream.flush()
        outputStream.close()
    }

    fun writeTemplate(type: Int, outputStream: FileOutputStream, app: SaveAppApplication) {
        val header = when (type) {
            CREATE_MOVEMENTS_TEMPLATE -> app.getString(R.string.movements_export_header)
            CREATE_SUBSCRIPTIONS_TEMPLATE -> app.getString(R.string.subscriptions_export_header)
            else -> app.getString(R.string.budgets_export_header)
        }

        outputStream.write(header.toByteArray())

        outputStream.flush()
        outputStream.close()
    }

    fun getFromFile(dataType: Int, activity: MainActivity) {
        val filter = "text/comma-separated-values"
        when (dataType) {
            OPEN_MOVEMENTS_FILE -> activity.importMovements.launch(filter)
            OPEN_SUBSCRIPTIONS_FILE -> activity.importSubscriptions.launch(filter)
            OPEN_BUDGETS_FILE -> activity.importBudgets.launch(filter)
        }
    }

    fun import(type: Int, inputStream: FileInputStream, app: SaveAppApplication) {
        val reader = inputStream.bufferedReader()

        // Skip header line
        reader.readLine()
        when (type) {
            OPEN_MOVEMENTS_FILE -> importMovements(reader, app)
            OPEN_SUBSCRIPTIONS_FILE -> importSubscriptions(reader, app)
            OPEN_BUDGETS_FILE -> importBudgets(reader, app)
        }

        reader.close()
        inputStream.close()
    }

    private fun exportMovements(writer: BufferedWriter, app: SaveAppApplication) {
        val movements = runBlocking {
            val repo = app.movementRepository
            val tagRepo = app.tagRepository
            val result = repo.getAll()
            if (result is NetworkResult.Success) {
                result.data.map {
                    val tag = tagRepo.getByName(it.expenseType ?: "")
                    it.mapToTaggedMovement(tag)
                }
            } else {
                emptyList()
            }
        }

        writer.write(app.getString(R.string.movements_export_header))
        writer.newLine()
        for (m in movements) {
            writer.write("${m.id},${m.amount},${m.description},${m.date},${m.tagId},${m.budgetId}")
            writer.newLine()
        }
    }

    private fun exportSubscriptions(writer: BufferedWriter, app: SaveAppApplication) {
        val subscriptions = runBlocking {
            val repo = app.subscriptionRepository
            val tagRepo = app.tagRepository
            val result = repo.getAll()
            if(result is NetworkResult.Success) {
                result.data
            }else
                emptyList()
        }

        writer.write(app.getString(R.string.subscriptions_export_header))
        writer.newLine()
        for (s in subscriptions) {
            writer.write(
                "${s.id},${s.name},${s.amount},${s.renewalAfter}," +
                        "${s.lastPaid},${s.subscriptionType}"
            )
            writer.newLine()
        }
    }

    private fun exportBudgets(writer: BufferedWriter, app: SaveAppApplication) {
        val budgets = runBlocking {
            val result = app.remoteBudgetRepository.getAll()
            if (result is NetworkResult.Success) {
                result.data.map {
                    it.mapToBudget()
                }
            } else emptyList()
        }

        writer.write(app.getString(R.string.budgets_export_header))
        writer.newLine()
        for (b in budgets) {
            writer.write("${b.id},${b.max},${b.used},${b.name},${b.from},${b.to}")
            writer.newLine()
        }
    }

    private fun importMovements(reader: BufferedReader, app: SaveAppApplication) {
        val addMovements: MutableList<Movement> = mutableListOf()
        val updateMovements: MutableList<Movement> = mutableListOf()

        try {
            reader.lines().forEach {
                if (it != null) {
                    val m = it.toMovementOrNull()
                    if (m != null) {
                        if (m.id == 0) {
                            addMovements.add(m)
                        } else {
                            updateMovements.add(m)
                        }
                    }
                }
            }
            app.applicationScope.launch {
                addMovements.forEach {
                    BudgetUtil.tryAddMovementToBudget(it)
                    val tag = app.tagRepository.getById(it.tagId)
                    app.movementRepository.insert(it.mapToRemoteExpense(tag?.name ?: ""))
                    StatsUtil.addMovementToStat(app, it)
                }
                updateMovements.forEach {
                    val result = app.movementRepository.getById(it.id)
                    if (result !is NetworkResult.Success)
                        return@launch

                    val tag = app.tagRepository.getByName(result.data.expenseType ?: "")
                    val oldMovement = result.data.mapToMovement(tag)
                    if (it.budgetId != 0) {
                        BudgetUtil.removeMovementFromBudget(oldMovement)
                        BudgetUtil.tryAddMovementToBudget(it)
                    }

                    oldMovement.amount *= -1
                    StatsUtil.addMovementToStat(app, oldMovement)

                    app.movementRepository.update(it.mapToRemoteExpense(tag?.name ?: ""))
                    StatsUtil.addMovementToStat(app, it)
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun importSubscriptions(reader: BufferedReader, app: SaveAppApplication) {
        val addSubscriptions: MutableList<RemoteSubscription> = mutableListOf()
        val updateSubscriptions: MutableList<RemoteSubscription> = mutableListOf()

        try {
            reader.lines().forEach {
                if (it != null) {
                    val s = it.toRemoteSubscriptionOrNull()
                    if (s != null) {
                        if (s.id == 0) {
                            addSubscriptions.add(s)
                        } else {
                            updateSubscriptions.add(s)
                        }
                    }
                }
            }
            app.applicationScope.launch {
                addSubscriptions.forEach {
                    app.subscriptionRepository.insert(it)
                }
                updateSubscriptions.forEach {
                    app.subscriptionRepository.update(it)
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun importBudgets(reader: BufferedReader, app: SaveAppApplication) {
        val addBudgets: MutableList<Budget> = mutableListOf()
        val updateBudgets: MutableList<Budget> = mutableListOf()

        try {
            reader.lines().forEach {
                if (it != null) {
                    val b = it.toBudgetOrNull()
                    if (b != null) {
                        if (b.id == 0) {
                            addBudgets.add(b)
                        } else {
                            updateBudgets.add(b)
                        }
                    }
                }
            }
            app.applicationScope.launch {
                addBudgets.forEach {
                    app.remoteBudgetRepository.insert(it.mapToRemoteBudget())
                }
                updateBudgets.forEach {
                    app.remoteBudgetRepository.update(it.mapToRemoteBudget())
                }
            }
        } catch (_: Exception) {
        }
    }
}
