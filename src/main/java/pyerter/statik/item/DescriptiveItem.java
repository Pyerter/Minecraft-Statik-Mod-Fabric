package pyerter.statik.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.Desc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DescriptiveItem extends Item {
    public final List<Pair<String, Optional<Formatting[]>>> description;

    public DescriptiveItem(Settings settings) {
        super(settings);
        description = List.of();
    }

    public DescriptiveItem(Settings settings, List<Pair<String, Optional<Formatting[]>>> description) {
        super(settings);
        this.description = description;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        for (Pair<String, Optional<Formatting[]>> val: description) {
            MutableText text = MutableText.of(new LiteralTextContent(val.getLeft()));
            if (val.getRight().isPresent()) for (Formatting f: val.getRight().get())
                text = text.formatted(f);
            tooltip.add(text);
        }
    }

    public static class DescriptionBuilder {
        private List<Pair<String, Optional<Formatting[]>>> description;

        private DescriptionBuilder() {
            description = new LinkedList<>();
        }

        public static DescriptionBuilder start() {
            return new DescriptionBuilder();
        }

        public DescriptionBuilder add(String s, Formatting ... f) {
            this.description.add(new Pair<>(s, f.length > 0 ? Optional.of(f) : Optional.empty()));
            return this;
        }

        public List<Pair<String, Optional<Formatting[]>>> build() {
            return description;
        }
    }
}