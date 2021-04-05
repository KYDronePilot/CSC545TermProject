-- Reference: how to make a column an identity column: https://oracle-base.com/articles/12c/identity-columns-in-oracle-12cr1
create table NutritionFacts (
    id number generated always as identity primary key,
    calories number(*, 0) default 0,
    sugar number(*, 0) default 0,
    protein number(*, 0) default 0,
    sodium number(*, 0) default 0,
    fat number(*, 0) default 0
);

create table FoodItem (
    id number generated always as identity primary key,
    name varchar2(50),
    nutritionFactsId number,
    foodGroup varchar2(30),
    constraint nutritionFactsFk foreign key (nutritionFactsId) references NutritionFacts(id) on delete cascade
);

create table Recipe (
    id number generated always as identity primary key,
    name varchar2(100),
    instructions clob,
    category varchar2(60)
);

create table RecipeFoodItem (
    recipeId number,
    foodItemId number,
    constraint recipeFk foreign key (recipeId) references Recipe(id) on delete cascade,
    constraint foodItemFk foreign key (foodItemId) references FoodItem(id) on delete cascade
);

create table DayMealPlan (
    id number generated always as identity primary key,
    breakfastRecipeId number,
    lunchRecipeId number,
    dinnerRecipeId number,
    constraint breakfastRecipeFk foreign key (breakfastRecipeId) references Recipe(id) on delete set null,
    constraint lunchRecipeFk foreign key (lunchRecipeId) references Recipe(id) on delete set null,
    constraint dinnerRecipeFk foreign key (dinnerRecipeId) references Recipe(id) on delete set null
);

create table WeekMealPlan (
    id number generated always as identity primary key,
    mondayRecipeId number,
    tuesdayRecipeId number,
    wednesdayRecipeId number,
    thursdayRecipeId number,
    fridayRecipeId number,
    saturdayRecipeId number,
    sundayRecipeId number,
    constraint mondayRecipeFk foreign key (mondayRecipeId) references Recipe(id) on delete set null,
    constraint tuesdayRecipeFk foreign key (tuesdayRecipeId) references Recipe(id) on delete set null,
    constraint wednesdayRecipeFk foreign key (wednesdayRecipeId) references Recipe(id) on delete set null,
    constraint thursdayRecipeFk foreign key (thursdayRecipeId) references Recipe(id) on delete set null,
    constraint fridayRecipeFk foreign key (fridayRecipeId) references Recipe(id) on delete set null,
    constraint saturdayRecipeFk foreign key (saturdayRecipeId) references Recipe(id) on delete set null,
    constraint sundayRecipeFk foreign key (sundayRecipeId) references Recipe(id) on delete set null
);
