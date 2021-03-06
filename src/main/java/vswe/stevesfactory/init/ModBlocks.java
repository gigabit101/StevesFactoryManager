package vswe.stevesfactory.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.GameData;
import reborncore.RebornRegistry;
import reborncore.common.util.RebornCraftingHelper;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.*;
import vswe.stevesfactory.items.ItemMemoryDisk;
import vswe.stevesfactory.tiles.*;

public final class ModBlocks
{
    public static final byte NBT_CURRENT_PROTOCOL_VERSION = 13;
    public static final String NBT_PROTOCOL_VERSION = "ProtocolVersion";

    private static final String MANAGER_TILE_ENTITY_TAG = "TileEntityMachineManagerName";
    public static final String MANAGER_NAME_TAG = "BlockMachineManagerName";
    public static final String MANAGER_UNLOCALIZED_NAME = "BlockMachineManager";

    public static final String CABLE_NAME_TAG = "BlockCableName";
    public static final String CABLE_UNLOCALIZED_NAME = "BlockCable";

    private static final String CABLE_RELAY_TILE_ENTITY_TAG = "TileEntityCableRelayName";
    public static final String CABLE_RELAY_NAME_TAG = "BlockCableRelayName";
    public static final String CABLE_RELAY_UNLOCALIZED_NAME = "BlockCableRelay";
    public static final String CABLE_ADVANCED_RELAY_UNLOCALIZED_NAME = "BlockAdvancedCableRelay";

    private static final String CABLE_OUTPUT_TILE_ENTITY_TAG = "TileEntityCableOutputName";
    public static final String CABLE_OUTPUT_NAME_TAG = "BlockCableOutputName";
    public static final String CABLE_OUTPUT_UNLOCALIZED_NAME = "BlockCableOutput";

    private static final String CABLE_INPUT_TILE_ENTITY_TAG = "TileEntityCableInputName";
    public static final String CABLE_INPUT_NAME_TAG = "BlockCableInputName";
    public static final String CABLE_INPUT_UNLOCALIZED_NAME = "BlockCableInput";

    private static final String CABLE_INTAKE_TILE_ENTITY_TAG = "TileEntityCableIntakeName";
    public static final String CABLE_INTAKE_NAME_TAG = "BlockCableIntakeName";
    public static final String CABLE_INTAKE_UNLOCALIZED_NAME = "BlockCableIntake";
    public static final String CABLE_INSTANT_INTAKE_UNLOCALIZED_NAME = "BlockInstantCableIntake";

    private static final String CABLE_BUD_TILE_ENTITY_TAG = "TileEntityCableBUDName";
    public static final String CABLE_BUD_NAME_TAG = "BlockCableBUDName";
    public static final String CABLE_BUD_UNLOCALIZED_NAME = "BlockCableBUD";

    private static final String CABLE_BREAKER_TILE_ENTITY_TAG = "TileEntityCableBreakerName";
    public static final String CABLE_BREAKER_NAME_TAG = "BlockCableBreakerName";
    public static final String CABLE_BREAKER_UNLOCALIZED_NAME = "BlockCableBreaker";

    private static final String CABLE_CLUSTER_TILE_ENTITY_TAG = "TileEntityCableClusterName";
    public static final String CABLE_CLUSTER_NAME_TAG = "BlockCableClusterName";
    public static final String CABLE_CLUSTER_UNLOCALIZED_NAME = "BlockCableCluster";
    public static final String CABLE_ADVANCED_CLUSTER_UNLOCALIZED_NAME = "BlockAdvancedCableCluster";

    private static final String CABLE_CAMOUFLAGE_TILE_ENTITY_TAG = "TileEntityCableCamouflageName";
    public static final String CABLE_CAMOUFLAGE_NAME_TAG = "BlockCableCamouflageName";

    private static final String CABLE_SIGN_TILE_ENTITY_TAG = "TileEntityCableSignName";
    public static final String CABLE_SIGN_NAME_TAG = "BlockCableSignName";
    public static final String CABLE_SIGN_UNLOCALIZED_NAME = "BlockCableSign";


    public static BlockManager blockManager;
    public static BlockCable blockCable;
    public static BlockCableRelay blockCableRelay;
    public static BlockCableOutput blockCableOutput;
    public static BlockCableInput blockCableInput;
    public static BlockCableIntake blockCableIntake;
    public static BlockCableBUD blockCableBUD;
    public static BlockCableBreaker blockCableBreaker;
    public static BlockCableCluster blockCableCluster;
    public static BlockCableCamouflages blockCableCamouflage;
    public static BlockCableSign blockCableSign;

    public static ItemMemoryDisk itemMemoryDisk;

    public static CreativeTabs creativeTab;


    public static void init()
    {
        creativeTab = new CreativeTabs("sfm")
        {
            @Override
            public ItemStack getIconItemStack()
            {
                return new ItemStack(blockManager);
            }

            @Override
            public ItemStack getTabIconItem()
            {
                return ItemStack.EMPTY;
            }
        };

        blockManager = new BlockManager();
        RebornRegistry.registerBlock(blockManager, MANAGER_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityManager.class, MANAGER_TILE_ENTITY_TAG);

        blockCable = new BlockCable();
        RebornRegistry.registerBlock(blockCable, CABLE_NAME_TAG);

        blockCableRelay = new BlockCableRelay();
        RebornRegistry.registerBlock(blockCableRelay, ItemRelay.class, CABLE_RELAY_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityRelay.class, CABLE_RELAY_TILE_ENTITY_TAG);
        ClusterRegistry.register(new ClusterRegistry.ClusterRegistryAdvancedSensitive(TileEntityRelay.class, blockCableRelay, new ItemStack(blockCableRelay, 1, 0)));
        ClusterRegistry.register(new ClusterRegistry.ClusterRegistryAdvancedSensitive(TileEntityRelay.class, blockCableRelay, new ItemStack(blockCableRelay, 1, 8)));

        blockCableOutput = new BlockCableOutput();
        RebornRegistry.registerBlock(blockCableOutput, CABLE_OUTPUT_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityOutput.class, CABLE_OUTPUT_TILE_ENTITY_TAG);
        ClusterRegistry.register(TileEntityOutput.class, blockCableOutput);

        blockCableInput = new BlockCableInput();
        RebornRegistry.registerBlock(blockCableInput, CABLE_INPUT_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityInput.class, CABLE_INPUT_TILE_ENTITY_TAG);
        ClusterRegistry.register(TileEntityInput.class, blockCableInput);

        blockCableIntake = new BlockCableIntake();
        RebornRegistry.registerBlock(blockCableIntake, ItemIntake.class, CABLE_INTAKE_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityIntake.class, CABLE_INTAKE_TILE_ENTITY_TAG);
        ClusterRegistry.register(new ClusterRegistry.ClusterRegistryAdvancedSensitive(TileEntityIntake.class, blockCableIntake, new ItemStack(blockCableIntake, 1, 0)));
        ClusterRegistry.register(new ClusterRegistry.ClusterRegistryAdvancedSensitive(TileEntityIntake.class, blockCableIntake, new ItemStack(blockCableIntake, 1, 8)));

        blockCableBUD = new BlockCableBUD();
        RebornRegistry.registerBlock(blockCableBUD, CABLE_BUD_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityBUD.class, CABLE_BUD_TILE_ENTITY_TAG);
        ClusterRegistry.register(TileEntityBUD.class, blockCableBUD);

//        blockCableBreaker = new BlockCableBreaker();
//        RebornRegistry.registerBlock(blockCableBreaker, CABLE_BREAKER_NAME_TAG);
//        GameRegistry.registerTileEntity(TileEntityBreaker.class, CABLE_BREAKER_TILE_ENTITY_TAG);
//        ClusterRegistry.register(TileEntityBreaker.class, blockCableBreaker);

        blockCableCluster = new BlockCableCluster();
        RebornRegistry.registerBlock(blockCableCluster, ItemCluster.class, CABLE_CLUSTER_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityCluster.class, CABLE_CLUSTER_TILE_ENTITY_TAG);

        blockCableCamouflage = new BlockCableCamouflages();
        RebornRegistry.registerBlock(blockCableCamouflage, ItemCamouflage.class, CABLE_CAMOUFLAGE_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityCamouflage.class, CABLE_CAMOUFLAGE_TILE_ENTITY_TAG);

        ClusterRegistry.register(new ClusterRegistry.ClusterRegistryMetaSensitive(TileEntityCamouflage.class, blockCableCamouflage, new ItemStack(blockCableCamouflage, 1, 0)));
        ClusterRegistry.register(new ClusterRegistry.ClusterRegistryMetaSensitive(TileEntityCamouflage.class, blockCableCamouflage, new ItemStack(blockCableCamouflage, 1, 1)));
        ClusterRegistry.register(new ClusterRegistry.ClusterRegistryMetaSensitive(TileEntityCamouflage.class, blockCableCamouflage, new ItemStack(blockCableCamouflage, 1, 2)));

        blockCableSign = new BlockCableSign();
        RebornRegistry.registerBlock(blockCableSign, CABLE_SIGN_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntitySignUpdater.class, CABLE_SIGN_TILE_ENTITY_TAG);
        ClusterRegistry.register(TileEntitySignUpdater.class, blockCableSign);

        itemMemoryDisk = new ItemMemoryDisk();
        RebornRegistry.registerItem(itemMemoryDisk);
    }

    public static void addRecipes()
    {
        RebornCraftingHelper.addShapedOreRecipe(new ItemStack(blockManager),
                "III",
                "IRI",
                "SPS",
                'R', Blocks.REDSTONE_BLOCK,
                'P', Blocks.PISTON,
                'I', Items.IRON_INGOT,
                'S', Blocks.STONE
        );

        RebornCraftingHelper.addShapedOreRecipe(new ItemStack(blockCable, 8),
                "GPG",
                "IRI",
                "GPG",
                'R', Items.REDSTONE,
                'G', Blocks.GLASS,
                'I', Items.IRON_INGOT,
                'P', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE
        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableRelay, 1),
                blockCable,
                Blocks.HOPPER
        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableOutput, 1),
                blockCable,
                Items.REDSTONE,
                Items.REDSTONE,
                Items.REDSTONE
        );


        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableInput, 1),
                blockCable,
                Items.REDSTONE
        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableRelay, 1, 8),
                new ItemStack(blockCableRelay, 1, 0),
                new ItemStack(Items.DYE, 1, 4)
        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableIntake, 1, 0),
                blockCable,
                Blocks.HOPPER,
                Blocks.HOPPER,
                Blocks.DROPPER
        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableBUD, 1),
                blockCable,
                Items.QUARTZ,
                Items.QUARTZ,
                Items.QUARTZ
        );


//        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableBreaker, 1),
//                blockCable,
//                Items.IRON_PICKAXE,
//                Blocks.DISPENSER
//        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableIntake, 1, 8),
                new ItemStack(blockCableIntake, 1, 0),
                Items.GOLD_INGOT
        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableCluster, 1),
                blockCable,
                Items.ENDER_PEARL,
                Items.ENDER_PEARL,
                Items.ENDER_PEARL
        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableCamouflage, 1, 0),
                blockCable,
                new ItemStack(Blocks.WOOL, 1, 14),
                new ItemStack(Blocks.WOOL, 1, 13),
                new ItemStack(Blocks.WOOL, 1, 11)
        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableCamouflage, 1, 1),
                new ItemStack(blockCableCamouflage, 1, 0),
                new ItemStack(blockCableCamouflage, 1, 0),
                Blocks.IRON_BARS,
                Blocks.IRON_BARS
        );

        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableCamouflage, 1, 2),
                new ItemStack(blockCableCamouflage, 1, 1),
                Blocks.STICKY_PISTON
        );


        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(blockCableSign, 1),
                blockCable,
                new ItemStack(Items.DYE, 0),
                Items.FEATHER
        );

        RebornCraftingHelper.addShapedOreRecipe(new ItemStack(itemMemoryDisk), " x ", "xyx", " x ", 'x', "ingotIron", 'y', new ItemStack(ModBlocks.blockManager));
        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(itemMemoryDisk), new ItemStack(itemMemoryDisk));

//        GameData.register_impl(new ClusterUpgradeRecipe());
        GameData.register_impl(new ClusterRecipe(new ResourceLocation(StevesFactoryManager.UNLOCALIZED_START + "clusterrecipe")));
    }

    private ModBlocks() {}
}
