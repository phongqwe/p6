package test.di

import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.p6.ui.window.di.qualifiers.DefaultFocusStateMs
import com.qxdzbc.p6.ui.window.focus_state.WindowFocusState
import dagger.Provides
import test.test_implementation.FakeWindowFocusState

import dagger.Module
@Module
interface WindowStateModuleForTest {
    companion object {
        @Provides
        @DefaultFocusStateMs
        fun FocusStateMs(): Ms<WindowFocusState> {
            return ms(FakeWindowFocusState())
        }
    }
}

