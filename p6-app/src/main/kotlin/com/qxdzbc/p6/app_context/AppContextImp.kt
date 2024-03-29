package com.qxdzbc.p6.app_context

import com.qxdzbc.p6.di.qualifiers.Username
import javax.inject.Inject


class AppContextImp @Inject constructor(
    @Username
    override val username: String,
) : AppContext
