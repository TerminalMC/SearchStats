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

package dev.terminalmc.searchstats;

import dev.terminalmc.searchstats.platform.Services;
import dev.terminalmc.searchstats.util.ModLogger;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SearchStats {
    public static final String MOD_ID = "searchstats";
    public static final String MOD_NAME = "SearchStats";
    public static final ModLogger LOG = new ModLogger(MOD_NAME);
    public static final Path ROOT_PATH = Services.PLATFORM.getConfigDir().resolve("searchstats");

    public static @Nullable String lastWorld = null;
    public static boolean lastWorldIsServer = false;

    private static String searchString = "";

    public static void setSearchString(String s) {
        searchString = s.toLowerCase();
    }

    public static String getSearchString() {
        return searchString;
    }

    public static void init() {
    }

    public static void save(String general, String items, String mobs) {
        Path exportPath = ROOT_PATH.resolve(getWorldName());
        String fileNameFormat = getFileNameFormat();
        String generalFileName = String.format(
                fileNameFormat,
                Component.translatable("stat.generalButton").getString()
        );
        String itemsFileName = String.format(
                fileNameFormat,
                Component.translatable("stat.itemsButton").getString()
        );
        String mobsFileName = String.format(
                fileNameFormat,
                Component.translatable("stat.mobsButton").getString()
        );
        save(exportPath, generalFileName, general);
        save(exportPath, itemsFileName, items);
        save(exportPath, mobsFileName, mobs);
        Util.getPlatform().openPath(exportPath);
    }

    private static String getWorldName() {
        String worldName = "unknown";
        if (lastWorld != null && !lastWorld.isBlank()) {
            worldName = lastWorld;
        }
        return (lastWorldIsServer ? "server_" : "world_") + worldName;
    }

    private static @NotNull String getFileNameFormat() {
        String playerName = "player";
        if (Minecraft.getInstance().player != null) {
            playerName = Minecraft.getInstance().player.getGameProfile().getName();
        }
        return String.format(
                "%s_%s_%%s.txt",
                Component.translatable("gui.stats").getString(),
                playerName
        );
    }

    private static void save(Path dirPath, String filename, String content) {
        try {
            if (!Files.isDirectory(dirPath))
                Files.createDirectories(dirPath);
            Path file = dirPath.resolve(filename);
            Path tempFile = file.resolveSibling(file.getFileName() + ".tmp");
            try (
                    OutputStreamWriter writer = new OutputStreamWriter(
                            new FileOutputStream(tempFile.toFile()),
                            StandardCharsets.UTF_8
                    )
            ) {
                writer.write(content);
            } catch (IOException e) {
                throw new IOException(e);
            }
            Files.move(
                    tempFile,
                    file,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            LOG.error("Unable to save stats", e);
        }
    }
}