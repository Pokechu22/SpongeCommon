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
package org.spongepowered.common.mixin.core.block.tiles;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.BlockPos;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.data.util.DataQueries;
import org.spongepowered.common.data.util.DataUtil;
import org.spongepowered.common.data.util.NbtDataUtil;
import org.spongepowered.common.interfaces.block.tile.IMixinTileEntity;
import org.spongepowered.common.interfaces.data.IMixinCustomDataHolder;
import org.spongepowered.common.service.persistence.NbtTranslator;
import org.spongepowered.common.util.VecHelper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@NonnullByDefault
@Mixin(net.minecraft.tileentity.TileEntity.class)
public abstract class MixinTileEntity implements TileEntity, IMixinTileEntity {

    @Shadow protected boolean tileEntityInvalid;
    @Shadow protected net.minecraft.world.World worldObj;

    @Shadow public abstract void markDirty();
    @Shadow public abstract BlockPos getPos();
    @Shadow public abstract void writeToNBT(NBTTagCompound compound);

    @Override
    public Location<World> getLocation() {
        return new Location<World>((World) this.worldObj, VecHelper.toVector(this.getPos()));
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = new MemoryDataContainer();
        container.set(Location.WORLD_ID, ((World) this.worldObj).getUniqueId().toString());
        container.set(Location.POSITION_X, this.getPos().getX());
        container.set(Location.POSITION_Y, this.getPos().getY());
        container.set(Location.POSITION_Z, this.getPos().getZ());
        container.set(DataQueries.BLOCK_ENTITY_TILE_TYPE, this.getClass().getSimpleName());
        final NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        container.set(DataQueries.UNSAFE_NBT, NbtTranslator.getInstance().translateFrom(compound));
        final ImmutableList<DataManipulator<?, ?>> manipulators = ImmutableList.copyOf(getContainers());
        if (!manipulators.isEmpty()) {
            List<DataView> manipulatorview = DataUtil.getSerializedManipulatorList(manipulators);
            container.set(DataQueries.DATA_MANIPULATORS, manipulatorview);
        }
        return container;
    }

    @Override
    public boolean validateRawData(DataContainer container) {
        return container.contains(Location.WORLD_ID)
            && container.contains(Location.POSITION_X)
            && container.contains(Location.POSITION_Y)
            && container.contains(Location.POSITION_Z)
            && container.contains(DataQueries.BLOCK_ENTITY_TILE_TYPE)
            && container.contains(DataQueries.UNSAFE_NBT);
    }

    @Override
    public void setRawData(DataContainer container) throws InvalidDataException {

    }

    @Override
    public boolean isValid() {
        return !this.tileEntityInvalid;
    }

    @Override
    public void setValid(boolean valid) {
        this.tileEntityInvalid = valid;
    }

    /**
     * Hooks into vanilla's writeToNBT to call {@link #writeToNbt}.
     * <p>
     * <p> This makes it easier for other entity mixins to override writeToNBT without having to specify the <code>@Inject</code> annotation. </p>
     *
     * @param compound The compound vanilla writes to (unused because we write to SpongeData)
     * @param ci (Unused) callback info
     */
    @Inject(method = "Lnet/minecraft/tileentity/TileEntity;writeToNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("HEAD"))
    public void onWriteToNBT(NBTTagCompound compound, CallbackInfo ci) {
        this.writeToNbt(this.getSpongeData());
    }

    /**
     * Hooks into vanilla's readFromNBT to call {@link #readFromNbt}.
     * <p>
     * <p> This makes it easier for other entity mixins to override readFromNbt without having to specify the <code>@Inject</code> annotation. </p>
     *
     * @param compound The compound vanilla reads from (unused because we read from SpongeData)
     * @param ci (Unused) callback info
     */
    @Inject(method = "Lnet/minecraft/tileentity/TileEntity;readFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"))
    public void onReadFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        this.readFromNbt(this.getSpongeData());
    }

    /**
     * Read extra data (SpongeData) from the tile entity's NBT tag.
     *
     * @param compound The SpongeData compound to read from
     */
    @Override
    public void readFromNbt(NBTTagCompound compound) {
    }

    /**
     * Write extra data (SpongeData) to the tile entity's NBT tag.
     *
     * @param compound The SpongeData compound to write to
     */
    @Override
    public void writeToNbt(NBTTagCompound compound) {
        if (this instanceof IMixinCustomDataHolder) {
            final List<DataView> manipulatorViews = DataUtil.getSerializedManipulatorList(((IMixinCustomDataHolder) this).getCustomManipulators());
            final NBTTagList manipulatorTagList = new NBTTagList();
            for (DataView dataView : manipulatorViews) {
                manipulatorTagList.appendTag(NbtTranslator.getInstance().translateData(dataView));
            }
            compound.setTag(NbtDataUtil.CUSTOM_MANIPULATOR_TAG, manipulatorTagList);
        }
    }

    @Override
    public Collection<DataManipulator<?, ?>> getContainers() {
        return Lists.newArrayList(); // TODO override this in subclasses
    }
}
