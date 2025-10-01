/*
 * Copyright 2021 Guntram Blohm
 * Copyright 2025 TerminalMC
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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.terminalmc.searchstats.SearchStats;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.terminalmc.searchstats.util.Localization.localized;

@Mixin(StatsScreen.class)
public abstract class StatsScreenMixin extends Screen {

    @Shadow
    public abstract void onStatsUpdated();

    @Shadow
    private boolean isLoading;

    @Shadow
    @Nullable
    private TabNavigationBar tabNavigationBar;

    @Unique
    private EditBox searchstats$searchField;

    public StatsScreenMixin(Component text) {
        super(text);
    }

    /**
     * Clears the search query on screen init.
     */
    @Inject(
            method = "init",
            at = @At("RETURN")
    )
    private void afterInit(CallbackInfo ci) {
        SearchStats.setSearchString("");
    }

    @WrapOperation(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;addToFooter(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"
            )
    )
    private <T extends LayoutElement> T wrapAddDoneButton(
            HeaderAndFooterLayout instance,
            T child,
            Operation<T> original
    ) {
        int height = 18;
        int fieldWidth = 120;
        searchstats$searchField = new EditBox(font, fieldWidth, height, Component.empty());
        searchstats$searchField.setCanLoseFocus(false);
        searchstats$searchField.setFocused(true);
        searchstats$searchField.setHint(localized("hint"));

        LinearLayout layout = new LinearLayout(
                width,
                Math.max(height, child.getHeight()),
                Orientation.HORIZONTAL
        );
        layout.addChild(searchstats$searchField);

        if (child instanceof LinearLayout l) {
            l.visitChildren(layout::addChild);
        } else {
            if (child instanceof Button b)
                b.setWidth(Math.max(120, b.getWidth() / 2));
            layout.addChild(child);
        }

        original.call(instance, layout);
        return null;
    }

    /**
     * Directs keyboard inputs into the search field.
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (searchstats$searchField != null
                && searchstats$searchField.keyPressed(event)) {
            searchstats$refresh();
            return true;
        }
        return super.keyPressed(event);
    }

    /**
     * Directs keyboard inputs into the search field.
     */
    @Override
    public boolean charTyped(CharacterEvent event) {
        if (searchstats$searchField.charTyped(event)) {
            searchstats$refresh();
            return true;
        }
        return super.charTyped(event);
    }

    /**
     * Directs mouse inputs into the search field.
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (searchstats$searchField != null
                && searchstats$searchField.mouseClicked(event, doubleClick)) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Unique
    private void searchstats$refresh() {
        SearchStats.setSearchString(searchstats$searchField.getValue());
        int selected = SearchStats.selectedTab;
        isLoading = true;
        onStatsUpdated();
        if (selected >= 0 && tabNavigationBar != null)
            tabNavigationBar.selectTab(selected, false);
    }
}
