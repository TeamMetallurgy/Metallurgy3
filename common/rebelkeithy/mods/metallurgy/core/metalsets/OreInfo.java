package rebelkeithy.mods.metallurgy.core.metalsets;

import static rebelkeithy.mods.metallurgy.api.OreType.ALLOY;
import static rebelkeithy.mods.metallurgy.api.OreType.CATALYST;
import static rebelkeithy.mods.metallurgy.api.OreType.DROP;
import static rebelkeithy.mods.metallurgy.api.OreType.ORE;
import static rebelkeithy.mods.metallurgy.api.OreType.RESPAWN;

import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rebelkeithy.mods.keithyutils.items.CustomItemAxe;
import rebelkeithy.mods.keithyutils.items.CustomItemHoe;
import rebelkeithy.mods.keithyutils.items.CustomItemPickaxe;
import rebelkeithy.mods.keithyutils.items.CustomItemSpade;
import rebelkeithy.mods.keithyutils.metablock.MetaBlock;
import rebelkeithy.mods.keithyutils.metablock.SubBlock;
import rebelkeithy.mods.metallurgy.api.IOreInfo;
import rebelkeithy.mods.metallurgy.api.OreType;
import rebelkeithy.mods.metallurgy.core.MetallurgyCore;
import rebelkeithy.mods.metallurgy.core.database.MetalInfoDatabase;
import rebelkeithy.mods.metallurgy.machines.abstractor.AbstractorRecipes;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class OreInfo implements IOreInfo, IWorldGenerator
{
    private String setName;
    private String name;
    private String unlocalizedName;
    private boolean enabled;
    private CreativeTabs tab;
    private OreType type;
    private int oreID;
    private int oreMeta;
    private int blockID;
    private int blockMeta;
    private int brickID;
    private int brickMeta;
    private int itemIDs;

    private String dropName;
    private int dropMin;
    private int dropMax;

    private String[] alloyRecipe;
    private int abstractorXP;
    private int blockLvl;

    private int pickLvl;
    private int toolDura;
    private int toolDamage;
    private int toolSpeed;
    private int toolEnchant;

    private int helmetArmor;
    private int chestArmor;
    private int legsArmor;
    private int bootsArmor;
    private int armorDura;

    private int dungeonLootChance;
    private int dungeonLootAmount;
    private int veinCount;
    private int oreCount;
    private int minHeight;
    private int maxHeight;
    private int veinChance;
    private int veinDensity;
    private String[] diminsions;

    private SubBlock ore;
    private SubBlock block;
    private SubBlock brick;

    private Item dust;
    private Item ingot;

    private EnumToolMaterial toolEnum;
    private Item pickaxe;
    private Item shovel;
    private Item axe;
    private Item hoe;
    private ItemMetallurgySword sword;

    private Item helmet;
    private Item chest;
    private Item legs;
    private Item boots;
    
    private final MetalInfoDatabase dbMetal;
    
    static OreInfo EMPTY = new OreInfo()
    {
        public boolean isEnabled()
        {
            return false;
        }
    };

    private OreInfo()
    {
        dbMetal = null;
        // Use EMPTY for empties. Do not let anyone create another empty
    }

    OreInfo(Map<String, String> info, CreativeTabs tab, MetalInfoDatabase dbMetal)
    {
        this.dbMetal = dbMetal;
        setName = info.get("Metal Set");
        name = info.get("Name");
        unlocalizedName = name.replaceAll("\\s", "");
        unlocalizedName = unlocalizedName.substring(0, 1).toLowerCase() + unlocalizedName.substring(1);
        unlocalizedName = "metallurgy." + unlocalizedName;
        this.tab = tab;
        if (info.get("Type").equals("Ore"))
        {
            type = ORE;
        }
        else if (info.get("Type").equals("Catalyst"))
        {
            type = CATALYST;
        }
        else if (info.get("Type").equals("Alloy"))
        {
            type = ALLOY;
        }
        else if (info.get("Type").equals("Respawn"))
        {
            type = RESPAWN;
        }
        else if (info.get("Type").equals("Drop"))
        {
            type = DROP;
        }


        alloyRecipe = info.get("Alloy Recipe").split("\" \"");
        for (int n = 0; n < alloyRecipe.length; n++)
        {
            alloyRecipe[n] = "dust" + alloyRecipe[n].replace("\"", "");
        }

        if (type.generates())
        {
            oreID = Integer.parseInt(info.get("Ore ID").split(":")[0]);
            oreMeta = Integer.parseInt(info.get("Ore ID").split(":")[1]);
        }
        if (!info.get("Block ID").equals("0"))
        {
            blockID = Integer.parseInt(info.get("Block ID").split(":")[0]);
            blockMeta = Integer.parseInt(info.get("Block ID").split(":")[1]);
        }
        if (!info.get("Brick ID").equals("0"))
        {
            brickID = Integer.parseInt(info.get("Brick ID").split(":")[0]);
            brickMeta = Integer.parseInt(info.get("Brick ID").split(":")[1]);
        }

        if (type == DROP)
        {
            dropName = info.get("Drops").replace("\"", "");
            if (info.get("Drop Amount").contains("-"))
            {
                dropMin = Integer.parseInt(info.get("Drop Amount").split("-")[0]);
                dropMax = Integer.parseInt(info.get("Drop Amount").split("-")[1]);
            }
            else
            {
                dropMin = Integer.parseInt(info.get("Drop Amount"));
                dropMax = dropMin;
            }
        }
        else
        {
            dropName = null;
        }

        itemIDs = Integer.parseInt(info.get("Item IDs"));

        abstractorXP = Integer.parseInt(info.get("Abstractor XP"));
        blockLvl = Integer.parseInt(info.get("Block lvl"));

        if (type != CATALYST && type != DROP)
        {
            pickLvl = Integer.parseInt(info.get("Pick lvl"));
            toolDura = Integer.parseInt(info.get("Durability"));
            toolDamage = Integer.parseInt(info.get("Damage"));
            toolSpeed = Integer.parseInt(info.get("Speed"));
            toolEnchant = Integer.parseInt(info.get("Enchantability"));

            helmetArmor = Integer.parseInt(info.get("Helmet Armor"));
            chestArmor = Integer.parseInt(info.get("Chestplate Armor"));
            legsArmor = Integer.parseInt(info.get("Leggings Armor"));
            bootsArmor = Integer.parseInt(info.get("Boots Armor"));
            armorDura = Integer.parseInt(info.get("Durability Multiplier"));
        }

        dungeonLootChance = Integer.parseInt(info.get("Dungeon Loot Chance"));
        dungeonLootAmount = Integer.parseInt(info.get("Dungeon Loot Amount"));

        if (type.generates())
        {
            veinCount = Integer.parseInt(info.get("Veins Per Chunk"));
            oreCount = Integer.parseInt(info.get("Ores Per Vein"));
            minHeight = Integer.parseInt(info.get("Min Level"));
            maxHeight = Integer.parseInt(info.get("Max Level"));
            veinChance = Integer.parseInt(info.get("Vein Chance Per Chunk"));
            veinDensity = Integer.parseInt(info.get("Vein Density"));
            diminsions = info.get("Diminsions").split(" ");
        }

    }

    private void addRecipes()
    {
        if (!enabled)
        {
            return;
        }

        if (type.generates() && ore != null)
        {
            FurnaceRecipes.smelting().addSmelting(oreID, oreMeta, new ItemStack(ingot), 0);
        }

        FurnaceRecipes.smelting().addSmelting(dust.itemID, 0, new ItemStack(ingot), 0);

        ShapedOreRecipe recipe;
        if (block != null)
        {
            recipe = new ShapedOreRecipe(new ItemStack(blockID, 1, blockMeta), "XXX", "XXX", "XXX", 'X', "ingot" + name);
            GameRegistry.addRecipe(recipe);
            GameRegistry.addShapelessRecipe(new ItemStack(ingot, 9), new ItemStack(blockID, 1, blockMeta));
        }
        if (brick != null)
        {
            recipe = new ShapedOreRecipe(new ItemStack(brickID, 4, brickMeta), "XX", "XX", 'X', "ingot" + name);
            GameRegistry.addRecipe(recipe);
            GameRegistry.addShapelessRecipe(new ItemStack(ingot, 1), new ItemStack(brickID, 1, brickMeta));
        }

        if (type != CATALYST)
        {
        	if(MetallurgyCore.getConfigSettingBoolean("Tool Recipes", name, true)) {
	            recipe = new ShapedOreRecipe(new ItemStack(pickaxe), "XXX", " S ", " S ", 'X', "ingot" + name, 'S', Item.stick);
	            GameRegistry.addRecipe(recipe);
	            recipe = new ShapedOreRecipe(new ItemStack(shovel), "X", "S", "S", 'X', "ingot" + name, 'S', Item.stick);
	            GameRegistry.addRecipe(recipe);
	            recipe = new ShapedOreRecipe(new ItemStack(axe), "XX", "SX", "S ", 'X', "ingot" + name, 'S', Item.stick);
	            GameRegistry.addRecipe(recipe);
	            recipe = new ShapedOreRecipe(new ItemStack(hoe), "XX", "S ", "S ", 'X', "ingot" + name, 'S', Item.stick);
	            GameRegistry.addRecipe(recipe);
	            recipe = new ShapedOreRecipe(new ItemStack(sword), "X", "X", "S", 'X', "ingot" + name, 'S', Item.stick);
	            GameRegistry.addRecipe(recipe);
        	}
        	
            if(MetallurgyCore.getConfigSettingBoolean("Armour Recipes", name, true)) {
		        recipe = new ShapedOreRecipe(new ItemStack(helmet), "XXX", "X X", 'X', "ingot" + name);
		        GameRegistry.addRecipe(recipe);
		        recipe = new ShapedOreRecipe(new ItemStack(chest), "X X", "XXX", "XXX", 'X', "ingot" + name);
		        GameRegistry.addRecipe(recipe);
		        recipe = new ShapedOreRecipe(new ItemStack(legs), "XXX", "X X", "X X", 'X', "ingot" + name);
		        GameRegistry.addRecipe(recipe);
		        recipe = new ShapedOreRecipe(new ItemStack(boots), "X X", "X X", 'X', "ingot" + name);
		        GameRegistry.addRecipe(recipe);
            }
            if(MetallurgyCore.getConfigSettingBoolean("Features", "Bucket Recipes", true)) {
		        recipe = new ShapedOreRecipe(new ItemStack(Item.bucketEmpty), "X X", " X ", 'X', "ingot" + name);
		        GameRegistry.addRecipe(recipe);
            }
            
            recipe = new ShapedOreRecipe(new ItemStack(Item.shears), "X ", " X", 'X', "ingot" + name);
            GameRegistry.addRecipe(recipe);
        }

        if (type == ALLOY)
        {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(dust, 2), (Object[])alloyRecipe));
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!type.generates() || ore == null && type != RESPAWN)
        {
            return;
        }

        if (!spawnsInDim(world.provider.dimensionId))
        {
            return;
        }
        
        long worldSeed = world.getSeed();
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        
        long chunkSeed = (xSeed * chunkX + zSeed * chunkZ + oreID + oreMeta) ^ worldSeed;
        
        random.setSeed(chunkSeed);

        if (random.nextInt(100) > veinChance)
        {
            return;
        }
        
        chunkX = chunkX << 4;
        chunkZ = chunkZ << 4;

        WorldGenMinable worldGenMinable = new WorldGenMinable(oreID, oreMeta, oreCount + 1, Block.stone.blockID);
        
        if(world.provider.dimensionId == -1)
        {
        	worldGenMinable = new WorldGenMinable(oreID, oreMeta, oreCount + 1, Block.netherrack.blockID);
        }
        
        if(world.provider.dimensionId == 1)
        {
        	worldGenMinable = new WorldGenMinable(oreID, oreMeta, oreCount + 1, Block.whiteStone.blockID);
        }
        
        
        for (int i = 0; i < veinCount + 1; i ++)
        {
        	final int x = chunkX + random.nextInt(16);
        	final int y = random.nextInt(maxHeight - minHeight) + minHeight;
        	final int z = chunkZ + random.nextInt(16);
        	
        	worldGenMinable.generate(world, random, x, y, z);
        }
        
        
//        for (int n = 0; n < veinCount; n++)
//        {
//            final int randPosX = chunkX * 16 + random.nextInt(16);
//            final int randPosY = random.nextInt(maxHeight - minHeight) + minHeight;
//            final int randPosZ = chunkZ * 16 + random.nextInt(16);
//
//            // new WorldGenMinable(oreID, oreMeta, oreCount).generate(world,
//            // random, randPosX, randPosY, randPosZ);
//            new MetallurgyWorldGenMinable(oreID, oreMeta, oreCount, veinDensity, Block.stone.blockID, 0).generate(world, random, randPosX, randPosY, randPosZ);
//            // new WorldGenMinable(oreID, oreMeta, 40).generate(world, random,
//            // randPosX, randPosY, randPosZ);
//        }
    }

    @Override
    public String[] getAlloyRecipe()
    {
        return alloyRecipe;
    }

    @Override
    public ItemStack getBlock()
    {
        if (blockID != 0 && block != null)
        {
            return new ItemStack(blockID, 1, blockMeta);
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getBrick()
    {
        if (brickID != 0 && brick != null)
        {
            return new ItemStack(brickID, 1, brickMeta);
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getDrop()
    {
        if (dropName != null)
        {
            return dbMetal.getItem(dropName);
        }
        else
        {
            return null;
        }
    }

    @Override
    public int getDropAmountMax()
    {
        return dropMax;
    }

    @Override
    public int getDropAmountMin()
    {
        return dropMin;
    }

    @Override
    public ItemStack getDust()
    {
        if (dust != null)
        {
            return new ItemStack(dust);
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getIngot()
    {
        if (ingot != null)
        {
            return new ItemStack(ingot);
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public ItemStack getOre()
    {
        if (oreID != 0 && ore != null)
        {
            return new ItemStack(oreID, 1, oreMeta);
        }
        else
        {
            return null;
        }
    }

    @Override
    public OreType getType()
    {
        return type;
    }

    void init()
    {
        if (!enabled)
        {
            return;
        }
        
        if (!type.equals(RESPAWN))
        {
            final String iconNamePrefix = "Metallurgy:" + setName + "/" + name;
            if (type.generates() && oreID != 0)
            {
                ore = new SubBlock(oreID, oreMeta, iconNamePrefix + "Ore").setUnlocalizedName(unlocalizedName +  ".ore").setCreativeTab(tab);
                if (type == DROP)
                {
                    ore.setBlockDrops(dbMetal.getItem(dropName), dropMin, dropMax);
                }
            }
            if (type != DROP && blockID != 0)
            {
                block = new SubBlock(blockID, blockMeta, iconNamePrefix + "Block").setUnlocalizedName(unlocalizedName + ".block").setCreativeTab(tab);
            }
            if (type != DROP && brickID != 0)
            {
                brick = new SubBlock(brickID, brickMeta, iconNamePrefix + "Brick").setUnlocalizedName(unlocalizedName + ".bricks").setCreativeTab(tab);
            }
            if (type != DROP)
            {
                dust = new ItemMetallurgy(itemIDs).setTextureName(iconNamePrefix + "Dust").setUnlocalizedName(unlocalizedName + ".dust").setCreativeTab(tab);
                ingot = new ItemMetallurgy(itemIDs + 1).setTextureName(iconNamePrefix + "Ingot").setSmeltinExperience(abstractorXP / 3f)
                        .setUnlocalizedName(unlocalizedName + ".ingot").setCreativeTab(tab);
                AbstractorRecipes.addEssence(ingot.itemID, 0, abstractorXP);
            }

            if (type != CATALYST && type != DROP)
            {
                toolEnum = EnumHelper.addToolMaterial(name, pickLvl, toolDura, toolSpeed, toolDamage, toolEnchant);
                toolEnum.customCraftingMaterial = ingot;

                pickaxe = new CustomItemPickaxe(itemIDs + 2, toolEnum).setTextureName(iconNamePrefix + "Pick").setUnlocalizedName(unlocalizedName + ".pick")
                        .setCreativeTab(tab);
                shovel = new CustomItemSpade(itemIDs + 3, toolEnum).setTextureName(iconNamePrefix + "Shovel").setUnlocalizedName(unlocalizedName + ".shovel")
                        .setCreativeTab(tab);
                axe = new CustomItemAxe(itemIDs + 4, toolEnum).setTextureName(iconNamePrefix + "Axe").setUnlocalizedName(unlocalizedName + ".axe").setCreativeTab(tab);
                hoe = new CustomItemHoe(itemIDs + 5, toolEnum).setTextureName(iconNamePrefix + "Hoe").setUnlocalizedName(unlocalizedName + ".hoe").setCreativeTab(tab);
                sword = (ItemMetallurgySword) new ItemMetallurgySword(itemIDs + 6, toolEnum).setTextureName(iconNamePrefix + "Sword")
                        .setUnlocalizedName(unlocalizedName + ".sword").setCreativeTab(tab);

                final EnumArmorMaterial armorEnum = EnumHelper.addArmorMaterial(name, armorDura, new int[]
                { helmetArmor, chestArmor, legsArmor, bootsArmor }, toolEnchant);
                armorEnum.customCraftingMaterial = ingot;
                String armorTexture = name;
                armorTexture = armorTexture.replaceAll("\\s", "").toLowerCase();
                helmet = new ItemMetallurgyArmor(itemIDs + 7, armorEnum, 0, 0).setTextureFile(armorTexture + "_1").setTextureName(iconNamePrefix + "Helmet")
                        .setUnlocalizedName(unlocalizedName + ".helmet").setCreativeTab(tab);
                chest = new ItemMetallurgyArmor(itemIDs + 8, armorEnum, 1, 1).setTextureFile(armorTexture + "_1").setTextureName(iconNamePrefix + "Chest")
                        .setUnlocalizedName(unlocalizedName + ".chest").setCreativeTab(tab);
                legs = new ItemMetallurgyArmor(itemIDs + 9, armorEnum, 2, 2).setTextureFile(armorTexture + "_2").setTextureName(iconNamePrefix + "Legs")
                        .setUnlocalizedName(unlocalizedName + ".legs").setCreativeTab(tab);
                boots = new ItemMetallurgyArmor(itemIDs + 10, armorEnum, 3, 3).setTextureFile(armorTexture + "_1").setTextureName(iconNamePrefix + "Boots")
                        .setUnlocalizedName(unlocalizedName + ".boots").setCreativeTab(tab);
            }
        }

        if (type.generates())
        {
            GameRegistry.registerWorldGenerator(this);
        }
    }

    void initConfig(Configuration config)
    {
        enabled = config.get("!Enable.Enable Metals", "Enable " + name, true).getBoolean(true);

        final boolean setEnabled = config.get("!Enable", "Enable " + setName + " Set", true).getBoolean(true);

        enabled = enabled && setEnabled;

        if (!type.equals(RESPAWN))
        {
            String id;
            if ((type == ORE || type == CATALYST || type == DROP) && oreID != 0)
            {
                id = config.get("#Block IDs", name + " Ore", oreID + ":" + oreMeta).getString();
                oreID = Integer.parseInt(id.split(":")[0]);
                oreMeta = Integer.parseInt(id.split(":")[1]);
            }
            if (type != DROP && blockID != 0)
            {
                id = config.get("#Block IDs", name + " Block", blockID + ":" + blockMeta).getString();
                blockID = Integer.parseInt(id.split(":")[0]);
                blockMeta = Integer.parseInt(id.split(":")[1]);
            }
            if (type != DROP && brickID != 0)
            {
                id = config.get("#Block IDs", name + " Brick", brickID + ":" + brickMeta).getString();
                brickID = Integer.parseInt(id.split(":")[0]);
                brickMeta = Integer.parseInt(id.split(":")[1]);
            }

            if (type != DROP)
            {
                itemIDs = config.get("#Item IDs", name + " Item IDs (reserves 50)", itemIDs).getInt();

                abstractorXP = config.get(name + ".misc", "abstractor xp", abstractorXP).getInt();
            }

            blockLvl = config.get(name + ".misc", "Block Hardness Level", blockLvl).getInt();

            if (type != CATALYST && type != DROP)
            {
                pickLvl = config.get(name + ".Tool Info", "Pick Level", pickLvl).getInt();
                toolDura = config.get(name + ".Tool Info", "Durability", toolDura).getInt();
                toolDamage = config.get(name + ".Tool Info", "Damage", toolDamage).getInt();
                toolSpeed = config.get(name + ".Tool Info", "Speed", toolSpeed).getInt();
                toolEnchant = config.get(name + ".Tool Info", "Enchantability", toolEnchant).getInt();

                helmetArmor = config.get(name + ".Armor Info", "Helmet Armor", helmetArmor).getInt();
                chestArmor = config.get(name + ".Armor Info", "Chestplate Armor", chestArmor).getInt();
                legsArmor = config.get(name + ".Armor Info", "Leggings Armor", legsArmor).getInt();
                bootsArmor = config.get(name + ".Armor Info", "Boots Armor", bootsArmor).getInt();
                armorDura = config.get(name + ".Armor Info", "Durability Multiplier", armorDura).getInt();
            }

            if (type != DROP)
            {
                dungeonLootChance = config.get(name + ".World Gen", "Dungeon Loot Chance", dungeonLootChance).getInt();
                dungeonLootAmount = config.get(name + ".World Gen", "Dungeon Loot Amount", dungeonLootAmount).getInt();
            }
        }
        if (type.generates())
        {
            veinCount = config.get(name + ".World Gen", "Veins Per Chunk", veinCount).getInt();
            oreCount = config.get(name + ".World Gen", "Vein Size", oreCount).getInt();
            minHeight = config.get(name + ".World Gen", "Min Height", minHeight).getInt();
            maxHeight = config.get(name + ".World Gen", "Max Height", maxHeight).getInt();
            veinChance = config.get(name + ".World Gen", "Vein Chance", veinChance).getInt();
            veinDensity = config.get(name + ".World Gen", "Vein Density", veinDensity).getInt();

            String dimCombined = "";
            if (diminsions.length > 0)
            {
                dimCombined = diminsions[0];
                for (int n = 1; n < diminsions.length; n++)
                {
                    dimCombined += " " + diminsions[n];
                }
            }
            diminsions = config.get(name + ".World Gen", "Diminsions", dimCombined).getString().split(" ");
        }
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    void load()
    {
        if (!enabled)
        {
            return;
        }

        if (!type.equals(RESPAWN))
        {
            if (oreID != 0)
            {
                MetaBlock.registerID(oreID);
            }
            if (brickID != 0)
            {
                MetaBlock.registerID(brickID);
            }
            if (blockID != 0)
            {
                MetaBlock.registerID(blockID);
            }

            setLevels();
            if (type != DROP)
            {
                addRecipes();
            } else {
            	FurnaceRecipes.smelting().addSmelting(oreID, oreMeta, getDrop(), 0);
            }
            registerMetal();
        }
    }

    private void registerMetal()
    {
        if (!enabled)
        {
            return;
        }

        if (ore != null)
        {
            OreDictionary.registerOre("ore" + name, new ItemStack(oreID, 1, oreMeta));
        }
        if (block != null)
        {
            OreDictionary.registerOre("block" + name, new ItemStack(blockID, 1, blockMeta));
        }
        if (type != DROP)
        {
            OreDictionary.registerOre("ingot" + name, ingot);
            OreDictionary.registerOre("dust" + name, dust);
        }
    }

    private void setLevels()
    {
        if (!enabled)
        {
            return;
        }

        MinecraftForge.setToolClass(pickaxe, "pickaxe", pickLvl);
        if (ore != null)
        {
            MinecraftForge.setBlockHarvestLevel(ore.getBlock(), oreMeta, "pickaxe", blockLvl);
            ore.setHardness(2F).setResistance(1F);
        }
        if (block != null)
        {
            MinecraftForge.setBlockHarvestLevel(block.getBlock(), blockMeta, "pickaxe", blockLvl);
            block.setHardness(5F).setResistance(10F);
        }
        if (brick != null)
        {
            MinecraftForge.setBlockHarvestLevel(brick.getBlock(), brickMeta, "pickaxe", blockLvl);
            brick.setHardness(5F).setResistance(10F);
        }
    }

    private boolean spawnsInDim(int dim)
    {
        for (final String string : diminsions)
        {

            if (string.contains("-") && !string.startsWith("-"))
            {
                int min = Integer.parseInt(string.split("-")[0]);
                int max = Integer.parseInt(string.split("-")[1]);
                if (min > max)
                {
                    final int temp = min;
                    min = max;
                    max = temp;
                }
                if (dim >= min && dim <= max)
                {
                    return true;
                }
            }
            else
            {
                final int check = Integer.parseInt(string);
                if (dim == check) 
                {
                	return true;
                }
            }
        }
        return false;
    }

    public Item getAxe()
    {
        return axe;
    }

    public Item getBoots()
    {
        return boots;
    }

    public Item getChest()
    {
        return chest;
    }

    public Item getDustItem()
    {
        return dust;
    }

    public Item getHelmet()
    {
        return helmet;
    }

    public Item getHoe()
    {
        return hoe;
    }

    public Item getIngotItem()
    {
        return ingot;
    }

    public Item getLegs()
    {
        return legs;
    }

    public SubBlock getOreItem()
    {
        return ore;
    }

    int getOreMeta()
    {
        return oreMeta;
    }

    public Item getPickaxe()
    {
        return pickaxe;
    }

    public Item getShovel()
    {
        return shovel;
    }

    public ItemMetallurgySword getSword()
    {
        return sword;
    }
}
