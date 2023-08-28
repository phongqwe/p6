package com.qxdzbc.p6.ui.worksheet.slider.edge_slider.di

import com.qxdzbc.p6.ui.worksheet.slider.edge_slider.di.qualifiers.ForVerticalWsEdgeSlider
import com.qxdzbc.p6.ui.worksheet.slider.edge_slider.state.EdgeSliderState
import com.qxdzbc.p6.ui.worksheet.slider.edge_slider.state.ProjectThumbPosition
import dagger.Binds
import dagger.Module


@Module
interface EdgeSliderModule {

    @Binds
    @ForVerticalWsEdgeSlider
    fun projectasda(@ForVerticalWsEdgeSlider i:EdgeSliderState):ProjectThumbPosition

}