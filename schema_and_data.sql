--------------------------------------------------------------------------------
-- Table schema definition
--
-- Reference: How to make a column an identity column:
--   https://oracle-base.com/articles/12c/identity-columns-in-oracle-12cr1
--------------------------------------------------------------------------------

create table NutritionFacts (
    id number generated always as identity primary key,
    calories number(*, 0) not null,
    sugar number(*, 0) not null,
    protein number(*, 0) not null,
    sodium number(*, 0) not null,
    fat number(*, 0) not null
);

create table FoodItem (
    id number generated always as identity primary key,
    name varchar2(50) not null,
    foodGroup varchar2(30) not null,
    units number(*, 0) default 0 not null,
    nutritionFactsId number not null,
    constraint nutritionFactsFk foreign key (nutritionFactsId)
        references NutritionFacts(id) on delete cascade
);

create table Recipe (
    id number generated always as identity primary key,
    name varchar2(100) not null,
    instructions clob not null,
    category varchar2(60) not null
);

create table RecipeFoodItem (
    recipeId number,
    foodItemId number,
    primary key (recipeId, foodItemId),
    constraint foodItemRecipeFk foreign key (recipeId)
        references Recipe(id) on delete cascade,
    constraint recipeFoodItemFk foreign key (foodItemId)
        references FoodItem(id) on delete cascade
);

create table MealPlan (
    id number generated always as identity primary key,
    name varchar2(20) not null,
    day char(3) not null unique,
    constraint validateDayOfWeekCheck
        check (day in ('mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'))
);

create table RecipeMealPlan (
    recipeId number,
    mealPlanId number,
    primary key (recipeId, mealPlanId),
    meal varchar2(20) not null,
    constraint recipeFk foreign key (recipeId)
        references Recipe(id) on delete cascade,
    constraint mealPlanFk foreign key (mealPlanId)
        references MealPlan(id) on delete cascade
);

--------------------------------------------------------------------------------
-- Sample data for the application
--
-- IMPORTANT: These commands must be run sequentially only once. Otherwise, id
-- sequences will get messed up and constraints will fail. You can still drop
-- the tables and start over if you need to though.
--------------------------------------------------------------------------------

-- Sample nutrition facts of food items
insert into NutritionFacts (calories, sugar, protein, sodium, fat) values (200, 1, 8, 370, 3);
insert into NutritionFacts (calories, sugar, protein, sodium, fat) values (119, 0, 0, 0, 14);
insert into NutritionFacts (calories, sugar, protein, sodium, fat) values (7, 0, 0, 275, 0);
insert into NutritionFacts (calories, sugar, protein, sodium, fat) values (221, 1, 7, 8, 3);
insert into NutritionFacts (calories, sugar, protein, sodium, fat) values (498, 1, 54, 1550, 31);
insert into NutritionFacts (calories, sugar, protein, sodium, fat) values (12, 0, 1, 860, 0);
insert into NutritionFacts (calories, sugar, protein, sodium, fat) values (188, 0, 5, 891, 7);

-- Sample food items
insert into FoodItem (name, nutritionFactsId, foodGroup, units) values ('Italian bread', 1, 'grain/beans', 0);
insert into FoodItem (name, nutritionFactsId, foodGroup, units) values ('Olive oil', 2, 'other', 0);
insert into FoodItem (name, nutritionFactsId, foodGroup, units) values ('Bread dip spices', 3, 'other', 0);
insert into FoodItem (name, nutritionFactsId, foodGroup, units) values ('Egg noodles', 4, 'grain/beans', 0);
insert into FoodItem (name, nutritionFactsId, foodGroup, units) values ('Rotisserie chicken', 5, 'meat', 0);
insert into FoodItem (name, nutritionFactsId, foodGroup, units) values ('Chicken broth', 6, 'other', 0);
insert into FoodItem (name, nutritionFactsId, foodGroup, units) values ('Instant Ramen', 7, 'grain/beans', 0);

-- Sample recipes
insert into Recipe (name, instructions, category) values ('Italian bread dip', '- Mix the ingredients together
- Serve', 'appetizers');
insert into Recipe (name, instructions, category) values ('Chicken noodle soup', '- Debone chicken
- Heat chicken broth on burner
- Add noodles and let cook for 5 minutes
- Add chicken and wait 2 minutes
- Serve', 'meat - chicken');
insert into Recipe (name, instructions, category) values ('Ramen noodle soup', '- Heat water in microwave for 1.5 minutes
- Add spice and noodles
- Wait 5 minutes
- Serve', 'soups');

-- Recipe-food item links
insert into RecipeFoodItem (recipeId, foodItemId) values (1, 1);
insert into RecipeFoodItem (recipeId, foodItemId) values (1, 2);
insert into RecipeFoodItem (recipeId, foodItemId) values (1, 3);
insert into RecipeFoodItem (recipeId, foodItemId) values (2, 4);
insert into RecipeFoodItem (recipeId, foodItemId) values (2, 5);
insert into RecipeFoodItem (recipeId, foodItemId) values (2, 6);
insert into RecipeFoodItem (recipeId, foodItemId) values (3, 7);

-- Meal plans
insert into MealPlan (name, day) values ('Monday meals', 'mon');
insert into MealPlan (name, day) values ('Tuesday meals', 'tue');
insert into MealPlan (name, day) values ('Wednesday meals', 'wed');
insert into MealPlan (name, day) values ('Thursday meals', 'thu');
insert into MealPlan (name, day) values ('Friday meals', 'fri');
insert into MealPlan (name, day) values ('Saturday meals', 'sat');

-- Meal plan-recipe links
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (3, 1, 'breakfast');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (1, 1, 'lunch');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (2, 1, 'dinner');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (3, 2, 'breakfast');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (1, 2, 'lunch');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (2, 2, 'dinner');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (3, 3, 'breakfast');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (1, 3, 'lunch');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (2, 3, 'dinner');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (3, 4, 'breakfast');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (1, 4, 'lunch');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (2, 4, 'dinner');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (3, 5, 'breakfast');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (1, 5, 'lunch');
insert into RecipeMealPlan (recipeId, mealPlanId, meal) values (2, 5, 'dinner');

-- Commit to DB
commit;
