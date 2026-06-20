/*
 * Copyright 2021 Guntram Blohm
 * Copyright 2026 TerminalMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.terminalmc.searchstats.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.terminalmc.searchstats.SearchStats;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TabNavigationBar.class)
public abstract class TabNavigationBarMixin {

    @Shadow
    @Final
    protected ImmutableList<Tab> tabs;

    @WrapOperation(
            method = "selectTab",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/tabs/TabManager;setCurrentTab(Lnet/minecraft/client/gui/components/tabs/Tab;Z)V"
            )
    )
    private void wrapSetCurrentTab1(
            TabManager instance,
            Tab tab,
            boolean playClickSound,
            Operation<Void> original
    ) {
        SearchStats.selectedTab = tabs.indexOf(tab);
        original.call(instance, tab, playClickSound);
    }

    @WrapOperation(
            method = "setFocused(Lnet/minecraft/client/gui/components/events/GuiEventListener;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/tabs/TabManager;setCurrentTab(Lnet/minecraft/client/gui/components/tabs/Tab;Z)V"
            )
    )
    private void wrapSetCurrentTab2(
            TabManager instance,
            Tab tab,
            boolean playClickSound,
            Operation<Void> original
    ) {
        SearchStats.selectedTab = tabs.indexOf(tab);
        original.call(instance, tab, playClickSound);
    }
}
