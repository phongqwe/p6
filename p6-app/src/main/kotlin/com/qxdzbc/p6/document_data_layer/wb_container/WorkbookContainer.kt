package com.qxdzbc.p6.document_data_layer.wb_container

import com.qxdzbc.common.P6ExperimentalApi
import com.qxdzbc.common.Rse
import com.qxdzbc.common.WithSize
import com.qxdzbc.p6.document_data_layer.workbook.Workbook
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey

/**
 * An interface for getting, creating new, adding, removing [Workbook] only.
 */
interface WorkbookContainer : WorkbookGetter, WithSize {

    /**
     * Add a [Workbook] to this container, and create a new workbook state for that [Workbook] if necessary.
     */
    fun addWb(wb: Workbook)

    /**
     * Add a [Workbook] to this container, and create a new workbook state for that [Workbook] if necessary.
     * Return a [Rse] object showing whether the operation was successful or not.
     */
    fun addWbRs(wb: Workbook): Rse<Unit>

    /**
     * Overwrite the [Workbook] having the same key as [wb].
     */
    @P6ExperimentalApi("Use this function with care.The action of forced overwriting a workbook is very destructive. Must be considered careful before use")
    fun overwriteWb(wb: Workbook)

    /**
     * Overwrite a workbook having the same key as [wb], and create a new workbook state if necessary.
     * Return a [Rse] object showing whether the operation was successful or not.
     */
    @P6ExperimentalApi("Use this function with care.The action of forced overwriting a workbook is very destructive. Must be considered careful before use")
    fun overwriteWbRs(wb: Workbook): Rse<Unit>

    /**
     * Add or overwrite a [Workbook], then create a new wb state if no such state exist
     */
    @P6ExperimentalApi("Use this function with care.The action of forced overwriting a workbook is very destructive. Must be considered careful before use")
    fun addOrOverWriteWbRs(wb: Workbook): Rse<Unit>

    /**
     * Add or overwrite a [Workbook], then create a new wb state if a state for that [Workbook] does not exist
     */

    @P6ExperimentalApi("Use this function with care.The action of forced overwriting a workbook is very destructive. Must be considered careful before use")
    fun addOrOverWriteWb(wb: Workbook)

    /**
     * Remove the [Workbook] that has key == [wbKey]
     */
    fun removeWb(wbKey: WorkbookKey)

    /**
     * Remove the [Workbook] that has key == [wbKey].
     * Return a [Rse] object showing whether the operation was successful or not.
     */
    fun removeWbRs(wbKey: WorkbookKey): Rse<Unit>

    /**
     * Remove all workbook from this container
     */
    @Deprecated("don't use. It is dangerous to use this function because it creates inconsistency in the app state.")
    fun removeAll()

    /**
     * @return true if this container contains a [Workbook] with key == [wbKey]
     */
    fun containWb(wbKey: WorkbookKey): Boolean

    /**
     * Replay [WorkbookKey] of the workbook at [oldKey] with [newKey].
     */
    fun replaceKey(oldKey: WorkbookKey, newKey: WorkbookKey)

    /**
     * Replay [WorkbookKey] of the workbook at [oldKey] with [newKey].
     * Return a [Rse] object denoting whether the operation was a success or a failure.
     */
    fun replaceKeyRs(oldKey: WorkbookKey, newKey: WorkbookKey): Rse<Unit>
}
