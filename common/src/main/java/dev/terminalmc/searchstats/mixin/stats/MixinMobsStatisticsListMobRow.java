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

package dev.terminalmc.searchstats.mixin.stats;

import dev.terminalmc.searchstats.util.NamedStatEntry;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.client.gui.screens.achievement.StatsScreen$MobsStatisticsList$MobRow")
public class MixinMobsStatisticsListMobRow implements NamedStatEntry {
    @Shadow
    @Final
    public Component mobName;

    @Override
    public boolean searchstats$matchesSelection(String selection) {
        return mobName.getString().toLowerCase().contains(selection);
    }
}