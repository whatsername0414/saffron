package com.saffron.cook.core.data.repository.fake

import com.saffron.cook.core.domain.model.Category
import com.saffron.cook.core.domain.model.Difficulty
import com.saffron.cook.core.domain.model.Ingredient
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.Step
import com.saffron.cook.core.domain.repository.RecipeRepository

class FakeRecipeRepository(
    var shouldThrow: Boolean = false,
) : RecipeRepository {
    private val categories =
        listOf(
            Category("italian", "Italian"),
            Category("asian", "Asian"),
            Category("baking", "Baking"),
            Category("breakfast", "Breakfast"),
            Category("soups", "Soups"),
            Category("vegetarian", "Vegetarian"),
        )

    private val recipes =
        listOf(
            Recipe(
                id = "1",
                title = "Cacio e Pepe",
                description = "Rome's most deceptively simple pasta. Three ingredients — pasta, pecorino, black pepper — and technique is everything. The starch in the cooking water is the sauce.",
                imageUrl = "https://images.unsplash.com/photo-1612874742237-6526221588e3",
                categoryId = "italian",
                cookTimeMinutes = 20,
                servings = 2,
                difficulty = Difficulty.Medium,
                rating = 4.9f,
                ratingCount = 1284,
                isFeatured = true,
                ingredients =
                    listOf(
                        Ingredient("200 g", "spaghetti or tonnarelli"),
                        Ingredient("80 g", "Pecorino Romano, finely grated"),
                        Ingredient("20 g", "Parmigiano Reggiano, finely grated"),
                        Ingredient("2 tsp", "whole black peppercorns"),
                        Ingredient("1 tsp", "fine salt"),
                    ),
                steps =
                    listOf(
                        Step(
                            "Toast the pepper",
                            "Crack peppercorns coarsely — they should be uneven, not ground to dust. Toast in a dry wide pan over medium heat for ninety seconds until fragrant. Remove half and set aside.",
                        ),
                        Step(
                            "Cook the pasta",
                            "Bring a large pot of lightly salted water to a boil — use less salt than usual, as the cheese is very salty. Cook pasta two minutes short of the package time. Reserve at least 300 ml of starchy cooking water before draining.",
                        ),
                        Step(
                            "Build the sauce",
                            "Add a ladle of pasta water to the pepper in the pan and bring to a simmer. Add the drained pasta and toss constantly over medium heat, adding water a splash at a time, until the pasta is glossy and the sauce clings.",
                        ),
                        Step(
                            "Finish with cheese",
                            "Remove from heat. Stir in the grated cheeses in two additions, tossing vigorously and adding small amounts of water to prevent clumping. The sauce should be creamy, not thick. Serve immediately with the reserved pepper on top.",
                        ),
                    ),
            ),
            Recipe(
                id = "2",
                title = "Miso Glazed Salmon",
                description = "White miso, mirin, and a touch of honey create a lacquer-like glaze that caramelises under the grill. Ten minutes of preparation, fifteen of cooking.",
                imageUrl = "https://images.unsplash.com/photo-1467003909585-2f8a72700288",
                categoryId = "asian",
                cookTimeMinutes = 25,
                servings = 2,
                difficulty = Difficulty.Easy,
                rating = 4.7f,
                ratingCount = 892,
                ingredients =
                    listOf(
                        Ingredient("2", "salmon fillets, skin on (180 g each)"),
                        Ingredient("3 tbsp", "white miso paste"),
                        Ingredient("2 tbsp", "mirin"),
                        Ingredient("1 tbsp", "honey"),
                        Ingredient("1 tbsp", "soy sauce"),
                        Ingredient("1 tsp", "sesame oil"),
                        Ingredient("2", "spring onions, thinly sliced"),
                    ),
                steps =
                    listOf(
                        Step(
                            "Make the glaze",
                            "Whisk together miso, mirin, honey, soy sauce, and sesame oil until smooth. Taste — it should be salty, sweet, and slightly funky.",
                        ),
                        Step(
                            "Marinate",
                            "Pat the salmon dry. Coat flesh side generously with glaze. Marinate at room temperature for ten minutes, or refrigerate for up to four hours.",
                        ),
                        Step(
                            "Grill",
                            "Preheat the grill to high. Place salmon skin-side down on a foil-lined tray. Grill for ten to twelve minutes until the glaze is deeply caramelised and the fish flakes at its thickest point. Watch carefully — the honey burns quickly.",
                        ),
                        Step(
                            "Rest and serve",
                            "Rest for two minutes. Scatter with spring onions and serve over steamed rice with any remaining glaze spooned alongside.",
                        ),
                    ),
            ),
            Recipe(
                id = "3",
                title = "Brown Butter Banana Bread",
                description = "Browning the butter first adds a nutty depth that plain banana bread never has. Use the blackest bananas you can find — they're sweeter and more flavourful than ripe ones.",
                imageUrl = "https://images.unsplash.com/photo-1584736286279-f6c23e72d622",
                categoryId = "baking",
                cookTimeMinutes = 75,
                servings = 8,
                difficulty = Difficulty.Easy,
                rating = 4.8f,
                ratingCount = 2103,
                ingredients =
                    listOf(
                        Ingredient("3", "very ripe bananas (about 300 g peeled)"),
                        Ingredient("115 g", "unsalted butter"),
                        Ingredient("150 g", "light brown sugar"),
                        Ingredient("2", "large eggs"),
                        Ingredient("1 tsp", "vanilla extract"),
                        Ingredient("190 g", "plain flour"),
                        Ingredient("1 tsp", "bicarbonate of soda"),
                        Ingredient("½ tsp", "fine salt"),
                        Ingredient("½ tsp", "ground cinnamon"),
                    ),
                steps =
                    listOf(
                        Step(
                            "Brown the butter",
                            "Melt butter in a light-coloured saucepan over medium heat, swirling occasionally. After three to four minutes the foam will subside and the milk solids will turn golden brown and smell nutty. Pour immediately into a large bowl to stop cooking.",
                        ),
                        Step(
                            "Mash and mix",
                            "Mash bananas thoroughly into the brown butter — a few lumps are fine. Whisk in sugar, eggs, and vanilla until combined.",
                        ),
                        Step(
                            "Add dry ingredients",
                            "Sift flour, bicarbonate of soda, salt, and cinnamon into the wet ingredients. Fold until just combined — stop as soon as the flour disappears. Overmixing develops gluten and makes the loaf tough.",
                        ),
                        Step(
                            "Bake",
                            "Pour into a greased 22 × 12 cm loaf tin. Bake at 175°C (fan 155°C) for sixty to sixty-five minutes, until a skewer inserted in the centre comes out with a few moist crumbs. Cool in the tin for ten minutes before turning out.",
                        ),
                    ),
            ),
            Recipe(
                id = "4",
                title = "Shakshuka",
                description = "Eggs poached in a spiced tomato and pepper sauce. The sauce should be deeply flavoured and slightly sweet from long-cooked onions — the eggs finish in the residual heat.",
                imageUrl = "https://images.unsplash.com/photo-1590412200988-a436970781fa",
                categoryId = "breakfast",
                cookTimeMinutes = 35,
                servings = 2,
                difficulty = Difficulty.Easy,
                rating = 4.6f,
                ratingCount = 743,
                ingredients =
                    listOf(
                        Ingredient("4", "large eggs"),
                        Ingredient("400 g", "tinned whole tomatoes"),
                        Ingredient("2", "red peppers, thinly sliced"),
                        Ingredient("1", "large onion, thinly sliced"),
                        Ingredient("4", "garlic cloves, sliced"),
                        Ingredient("2 tbsp", "olive oil"),
                        Ingredient("1 tsp", "sweet smoked paprika"),
                        Ingredient("1 tsp", "ground cumin"),
                        Ingredient("½ tsp", "chilli flakes"),
                        Ingredient("small bunch", "flat-leaf parsley"),
                    ),
                steps =
                    listOf(
                        Step(
                            "Soften the vegetables",
                            "Warm olive oil in a wide, lidded frying pan over medium-low heat. Add onion and peppers with a pinch of salt. Cook for fifteen minutes, stirring occasionally, until very soft and beginning to caramelise. Add garlic and spices; cook for two minutes more.",
                        ),
                        Step(
                            "Build the sauce",
                            "Add tomatoes, crushing them with the back of a spoon. Season well. Simmer uncovered for ten minutes until the sauce thickens and the oil rises to the surface.",
                        ),
                        Step(
                            "Poach the eggs",
                            "Make four wells in the sauce. Crack an egg into each well. Cover and cook over low heat for five to six minutes, until the whites are just set but the yolks are still soft. Remove from heat — the eggs continue cooking in the pan.",
                        ),
                        Step(
                            "Finish and serve",
                            "Scatter with chopped parsley. Bring the pan straight to the table with warm bread for scooping.",
                        ),
                    ),
            ),
            Recipe(
                id = "5",
                title = "Roasted Tomato Soup",
                description = "Roasting concentrates the tomatoes until they're jammy and sweet. A small amount of cream and good bread is all you need alongside.",
                imageUrl = "https://images.unsplash.com/photo-1547592180-85f173990554",
                categoryId = "soups",
                cookTimeMinutes = 60,
                servings = 4,
                difficulty = Difficulty.Easy,
                rating = 4.5f,
                ratingCount = 516,
                ingredients =
                    listOf(
                        Ingredient("1 kg", "ripe tomatoes, halved"),
                        Ingredient("1", "head of garlic, top sliced off"),
                        Ingredient("1", "large onion, quartered"),
                        Ingredient("3 tbsp", "olive oil"),
                        Ingredient("500 ml", "vegetable stock"),
                        Ingredient("2 tbsp", "double cream"),
                        Ingredient("1 tsp", "caster sugar"),
                        Ingredient("small bunch", "fresh basil"),
                    ),
                steps =
                    listOf(
                        Step(
                            "Roast",
                            "Heat oven to 200°C. Place tomatoes cut-side up on a large roasting tray with the onion and garlic. Drizzle with olive oil and season well. Roast for forty to forty-five minutes until the tomatoes are collapsed and caramelised at the edges.",
                        ),
                        Step(
                            "Blend",
                            "Squeeze the roasted garlic cloves from their skins. Transfer everything from the tray — including any juices — to a blender. Add stock and most of the basil. Blend until very smooth.",
                        ),
                        Step(
                            "Finish",
                            "Pass through a sieve for a silky texture if you prefer. Return to a saucepan, stir in cream and sugar, and taste for seasoning. Warm over low heat without boiling. Serve with torn basil and good bread.",
                        ),
                    ),
            ),
            Recipe(
                id = "6",
                title = "Mushroom Risotto",
                description = "The key is patience and heat. The stock must go in warm, one ladle at a time, and the stirring is what releases the starch that makes risotto creamy — not cream.",
                imageUrl = "https://images.unsplash.com/photo-1476124369491-e7addf5db371",
                categoryId = "italian",
                cookTimeMinutes = 45,
                servings = 4,
                difficulty = Difficulty.Medium,
                rating = 4.7f,
                ratingCount = 961,
                ingredients =
                    listOf(
                        Ingredient("320 g", "Arborio or Carnaroli rice"),
                        Ingredient("400 g", "mixed mushrooms, sliced"),
                        Ingredient("1.2 L", "warm vegetable or chicken stock"),
                        Ingredient("1", "large onion, finely diced"),
                        Ingredient("3", "garlic cloves, minced"),
                        Ingredient("150 ml", "dry white wine"),
                        Ingredient("60 g", "unsalted butter"),
                        Ingredient("60 g", "Parmigiano Reggiano, grated"),
                        Ingredient("3 tbsp", "olive oil"),
                        Ingredient("small bunch", "fresh thyme"),
                    ),
                steps =
                    listOf(
                        Step(
                            "Sauté the mushrooms",
                            "Heat two tablespoons of olive oil in a large, wide pan over high heat until almost smoking. Add mushrooms in a single layer — do not crowd or they will steam. Cook without stirring for two minutes until golden. Season, add half the thyme, toss once, and transfer to a bowl.",
                        ),
                        Step(
                            "Start the base",
                            "Reduce heat to medium. Add remaining oil and half the butter. Cook onion for eight minutes until soft and translucent. Add garlic and remaining thyme; cook for one minute more.",
                        ),
                        Step(
                            "Toast the rice",
                            "Add rice and stir for two minutes until the edges become translucent. Pour in wine; stir until absorbed.",
                        ),
                        Step(
                            "Add stock gradually",
                            "Add warm stock one ladle at a time, stirring constantly and waiting until each addition is nearly absorbed before adding the next. This takes eighteen to twenty minutes. The rice is ready when it's creamy but still has a slight bite at its centre.",
                        ),
                        Step(
                            "Mantecare",
                            "Remove from heat. Fold in remaining cold butter and the Parmigiano in two additions, stirring vigorously — this is the mantecare, the emulsification that makes risotto glossy. Fold in the mushrooms. Rest for two minutes, then serve immediately.",
                        ),
                    ),
            ),
        )

    override suspend fun getRecipes(): List<Recipe> = guard { recipes }

    override suspend fun getRecipeById(id: String): Recipe? = guard { recipes.find { it.id == id } }

    override suspend fun getCategories(): List<Category> = guard { categories }

    override suspend fun getFeaturedRecipe(): Recipe? = guard { recipes.find { it.isFeatured } }

    override suspend fun getRecipesByCategory(categoryId: String): List<Recipe> = guard { recipes.filter { it.categoryId == categoryId } }

    override suspend fun searchRecipes(query: String): List<Recipe> =
        guard {
            val q = query.trim().lowercase()
            if (q.isEmpty()) {
                recipes
            } else {
                recipes.filter { recipe ->
                    recipe.title.lowercase().contains(q) ||
                        recipe.description.lowercase().contains(q) ||
                        recipe.ingredients.any { it.name.lowercase().contains(q) }
                }
            }
        }

    private inline fun <T> guard(block: () -> T): T {
        if (shouldThrow) throw IllegalStateException("FakeRecipeRepository error")
        return block()
    }
}
