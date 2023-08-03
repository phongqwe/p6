package  com.qxdzbc.p6

import androidx.compose.foundation.background
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.common.compose.StateUtils.rms
import com.qxdzbc.common.compose.StateUtils.toMs
import com.qxdzbc.common.compose.view.MBox
import com.qxdzbc.p6.app.app_context.P6GlobalAccessPoint
import com.qxdzbc.p6.app.document.workbook.Workbook
import com.qxdzbc.p6.app.document.workbook.WorkbookImp
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.app.err.ErrorContainer
import com.qxdzbc.p6.app.err.ErrorType
import com.qxdzbc.p6.di.DaggerP6Component
import com.qxdzbc.p6.di.P6Component
import com.qxdzbc.p6.ui.common.view.dialog.error.ErrorDialogWithStackTrace
import com.qxdzbc.p6.ui.document.workbook.state.WorkbookState
import com.qxdzbc.p6.ui.theme.P6Theme
import com.qxdzbc.p6.ui.window.WindowView
import com.qxdzbc.p6.ui.window.state.OuterWindowState
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val cs = this
        var p6Comp: P6Component? = null
        ColdInit()
        application {
            val appScope = this
            var starting by rms(true)
            // x: initialize the app
            LaunchedEffect(Unit) {
                val kernelCoroutineScope: CoroutineScope = cs

                val p6Comp2: P6Component = DaggerP6Component.builder()
                    .username("user_name")
                    .applicationCoroutineScope(kernelCoroutineScope)
                    .applicationScope(appScope)
                    .build()

                P6GlobalAccessPoint.setP6Component(p6Comp2)

                val appState = p6Comp2.appState()

                val wb1: Workbook = WorkbookImp(
                    keyMs = WorkbookKey("Book1", null).toMs(),
                ).apply {
                    listOf("Sheet1", "Sheet2").forEach { name ->
                        createNewWs(name)
                    }
                }

                val wb2 = WorkbookImp(
                    keyMs = WorkbookKey("Book2", null).toMs(),
                ).apply {
                    listOf("Sheet1", "Sheet2").forEach { name ->
                        createNewWs(name)
                    }
                }


                val window1Comp = p6Comp2.windowCompBuilder().build()

                val wbStateFactory = p6Comp2.wbStateFactory()

                val wbState1: WorkbookState = wbStateFactory.makeWbState(ms(wb1))
                val wbState2: WorkbookState = wbStateFactory.makeWbState(ms(wb2))

                appState.stateCont.wbStateCont.apply {
                    addOrOverwriteWbState(wbState1)
                    addOrOverwriteWbState(wbState2)
                }

                val windowState1 = window1Comp.windowState().apply {
                    addWbKey(wbState1.wbKeyMs)
                    addWbKey(wbState2.wbKeyMs)
                    activeWbPointerMs.value = activeWbPointer.pointTo(wbState1.wbKeyMs)
                }

                listOf(
                    ms(
                        p6Comp2.outerWindowStateFactory().create(windowState1) as OuterWindowState
                    )
                ).forEach {
                    appState.stateCont.addOuterWindowState(it)
                }

                p6Comp = p6Comp2

                val p6RpcServer = p6Comp2.p6RpcServer()

                cs.launch(Dispatchers.Default) {
                    p6RpcServer.start()
                }
                cs.launch {
                    delay(2000)
                    println("RPC server on ${p6RpcServer.server?.port}")
                    println("RPC is running: ${!(p6RpcServer.server?.isShutdown ?: true)}")
                }


                Runtime.getRuntime().addShutdownHook(Thread {
                    // x: kill kernel context when jvm stops
                    runBlocking {
                        p6RpcServer.stop()
                    }
                })

                starting = false
            }
            P6Theme {
                if (!starting) {
                    p6Comp?.also { p6c ->
                        val appState = p6c.appState()

                        for (windowOuterStateMs in appState.stateCont.outerWindowStateMsList) {
                            val windowState = windowOuterStateMs.value
                            val windowAction = p6c.windowActionTable().windowAction
                            val windowActionTable = p6c.windowActionTable()
                            WindowView(
                                state = windowState,
                                windowActionTable = windowActionTable,
                                windowAction = windowAction,
                            )
                        }

                        var appErrorContainer: ErrorContainer by appState.appErrorContainerMs
                        if (appErrorContainer.isNotEmpty()) {
                            for (bugMsg in appErrorContainer.errList) {
                                Window(
                                    onCloseRequest = { },
                                    title = "X",
                                ) {
                                    ErrorDialogWithStackTrace(
                                        errMsg = bugMsg,
                                        onOkClick = {
                                            when (bugMsg.type) {
                                                ErrorType.FATAL -> {
                                                    appErrorContainer.remove(bugMsg)
                                                    // x: Kill app when encounter fatal error
                                                    p6c.appAction().exitApp()
                                                }

                                                else -> appErrorContainer = appErrorContainer.remove(bugMsg)
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val windowState = rememberWindowState(size = DpSize(100.dp, 100.dp))
                    Window(
                        onCloseRequest = { },
                        title = "Starting",
                        state = windowState
                    ) {
                        MBox(modifier = Modifier.background(Color.Cyan)) {
                            Text("Starting")
                        }
                    }
                }

            }
        }
    }
}
