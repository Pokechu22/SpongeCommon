/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.profiler;

import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Mixin(Profiler.class)
public class MixinProfiler {

    /**
     * @author gabizou - January 22nd, 2017
     * @reason Clears out the profiler entirely. Since it's a debugging tool,
     * it's expensive to have it enabled when the user doesn't explicitly want it.
     */
    @Overwrite
    public void clearProfiling() {
    }

    /**
     * @author gabizou - January 22nd, 2017
     * @reason Clears out the profiler entirely. Since it's a debugging tool,
     * it's expensive to have it enabled when the user doesn't explicitly want it.
     */
    @Overwrite
    public void startSection(String name) {
    }

    /**
     * @author pokechu22 - February 9th, 2018
     * @reason Clears out the profiler entirely. Since it's a debugging tool,
     * it's expensive to have it enabled when the user doesn't explicitly want it.
     *
     * This method (which lacks MCP naming due to its introduction in 1.12.1)
     * normally evaluates the provided supplier only when profiling is enabled,
     * avoiding the costs of expensive name look ups (e.g. MC-117087).
     *
     * If we're disabling the profiler, we definitely want to disable the normally
     * expensive call to this supplier.
     */
    @Overwrite
    public void func_194340_a(Supplier<String> supplier) {
    }

    /**
     * @author gabizou - January 22nd, 2017
     * @reason Clears out the profiler entirely. Since it's a debugging tool,
     * it's expensive to have it enabled when the user doesn't explicitly want it.
     */
    @Overwrite
    public void endSection() {
    }

    /**
     * @author gabizou - January 22nd, 2017
     * @reason Clears out the profiler entirely. Since it's a debugging tool,
     * it's expensive to have it enabled when the user doesn't explicitly want it.
     */
    @Overwrite
    public List<Profiler.Result> getProfilingData(String profilerName) {
        return Collections.emptyList();
    }

    /**
     * @author gabizou - January 22nd, 2017
     * @reason Clears out the profiler entirely. Since it's a debugging tool,
     * it's expensive to have it enabled when the user doesn't explicitly want it.
     */
    @Overwrite
    public void endStartSection(String name) {
    }

    /**
     * @author gabizou - January 22nd, 2017
     * @reason Clears out the profiler entirely. Since it's a debugging tool,
     * it's expensive to have it enabled when the user doesn't explicitly want it.
     */
    @Overwrite
    public String getNameOfLastSection() {
        return "[DISABLED]";
    }

}
