## NutritionFacts
id -> calories, sugar, protein, sodium, fat
id determines all other attributes of NutritionFacts, so this would fall into BCNF.

## FoodItem
id -> name, foodGroup, units
id determines all other attributes of FoodItem, so this would fall into BCNF.

## Recipe
id -> name, instructions, category
id determines all other attributes of Recipe, so this would fall into BCNF.

## RecipeFoodItem

This table would fall under 3NF as its only two attributes are part of the superkey.

## MealPlan

id -> name, day
id determines all other attributes of MealPlan, so this would fall into BCNF.

## RecipleMealPlan

This table would fall under 3NF as its only two attributes are part of the superkey.