---
title: "CSC 545: Term Project"
subtitle: Schema Refinement and Justification
author:
    - Ivan Olivas
    - Trevor Cunagin
    - Ethen Holzapfel
    - Michael Galliers
date: \today{}
papersize: [letterpaper]
geometry: [margin=1in]
urlcolor: blue
fontsize: 12pt
indent: false
header-includes:
    - \newcommand{\ra}{\rightarrow}
    - \usepackage{setspace}
#   - \doublespacing
---

\maketitle
\thispagestyle{empty}
\clearpage

## Improved ER Diagram

[ER Diagram]

## Relational Schema

```sql
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
    constraint recipeFk foreign key (recipeId)
        references Recipe(id) on delete cascade,
    constraint mealPlanFk foreign key (mealPlanId)
        references MealPlan(id) on delete cascade
);
```

\doublespacing

## Justification

### `NutritionFacts`

$$id \rightarrow calories, sugar, protein, sodium, fat$$

$id$ determines all other attributes of `NutritionFacts`, so this would fall
into both _BCNF_ and _3NF_.

### `FoodItem`

$$id \rightarrow name, foodGroup, units, nutritionFactsId$$

$id$ determines all other attributes of `FoodItem`, so this would fall into both
_BCNF_ and _3NF_.

### `Recipe`

$$id \rightarrow name, instructions, category$$

$id$ determines all other attributes of `Recipe`, so this would fall into both
_BCNF_ and _3NF_.

### `RecipeFoodItem`

This table would fall under _BCNF_ since there are only two attributes.

### `MealPlan`

$$id \rightarrow name, day$$

$id$ determines all other attributes of `MealPlan`, so this would fall into both
_BCNF_ and _3NF_.

### `RecipeMealPlan`

$$recipeId, mealPlanId \rightarrow meal$$

This table would fall under _BCNF_ since $\{recipeId, mealPlanId\}$ is a
superkey.
