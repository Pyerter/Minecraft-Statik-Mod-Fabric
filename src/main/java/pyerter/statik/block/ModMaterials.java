package pyerter.statik.block;

import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.piston.PistonBehavior;

public class ModMaterials {

    public static final Material CRYSTALLIZED_MANA = (new Builder(MapColor.DIAMOND_BLUE)).lightPassesThrough().build();

    public static class Builder {
        private PistonBehavior pistonBehavior;
        private boolean blocksMovement;
        private boolean burnable;
        private boolean liquid;
        private boolean replaceable;
        private boolean solid;
        private final MapColor color;
        private boolean blocksLight;

        public Builder(MapColor color) {
            this.pistonBehavior = PistonBehavior.NORMAL;
            this.blocksMovement = true;
            this.solid = true;
            this.blocksLight = true;
            this.color = color;
        }

        public ModMaterials.Builder liquid() {
            this.liquid = true;
            return this;
        }

        public ModMaterials.Builder notSolid() {
            this.solid = false;
            return this;
        }

        public ModMaterials.Builder allowsMovement() {
            this.blocksMovement = false;
            return this;
        }

        ModMaterials.Builder lightPassesThrough() {
            this.blocksLight = false;
            return this;
        }

        protected ModMaterials.Builder burnable() {
            this.burnable = true;
            return this;
        }

        public ModMaterials.Builder replaceable() {
            this.replaceable = true;
            return this;
        }

        protected ModMaterials.Builder destroyedByPiston() {
            this.pistonBehavior = PistonBehavior.DESTROY;
            return this;
        }

        protected ModMaterials.Builder blocksPistons() {
            this.pistonBehavior = PistonBehavior.BLOCK;
            return this;
        }

        public Material build() {
            return new Material(this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.burnable, this.replaceable, this.pistonBehavior);
        }
    }

}
