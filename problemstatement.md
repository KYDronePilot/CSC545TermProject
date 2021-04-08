---
title: "CSC 545: Term Project"
subtitle: Problem Statement
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
---

\maketitle
\thispagestyle{empty}
\clearpage

This document sets forth a description of the problem being solved by the
program being written and assumptions made about how the solution will be
implemented.

## Problem Statement

We need an application that will allow for us to easily manage our weekly meal
plans for each day of the week. This application should allow for us to keep a
collection of recipes, track what ingredients we currently have, create meal
plans for each meal of the day, create a shopping list based on our supply of
ingredients, and search for recipes.

## Requirements

The following are key functionalities that the program will implement.

- Allow users to create, list, update, and delete food items.
- Allow users to create, list, update, and delete recipes.
- Allow users to create, list, update, and delete daily meal plans.
- Allow users to create, list, update, and delete weekly meal plans.
- Allow users to generate a shopping list based on a particular weekly meal
  plan and what food items the user already has.
- Allow users to search for recipes by food ingredients or recipe category.

## Assumptions

- The program will support managing multiple weekly meal plans.
- Rather than just tracking what items are specifically "in the fridge", the
  program will track whatever items the user "has" in general (including items
  that could be found in the pantry).
- The quantity of a food item that a user has will be tracked in a generic
  "units" quantity of type integer.
- If a recipe is listed as needing a particular ingredient, it is assumed that
  making the recipe will consume 1 unit of that ingredient.
- Each relation attribute will be explicitly constrained (unless otherwise
  obvious) with either `null` or `not null` for increased clarity.
- To simplify the program design, each core relation (not including M2M join
  tables) will have an `id` integer primary key that is automatically set when
  a row is created.
