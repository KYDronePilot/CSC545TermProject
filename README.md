---
title: "CSC 545: Term Project"
subtitle: Java Project README
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

## References

### Try-with-resource

This project makes use of the `try-with-resource` statement to automatically
close opened resources. Learned how to use from the following source:
<https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html>

### Lambda functions

Lambda functions (the equivalent of
[Swift Closures](https://docs.swift.org/swift-book/LanguageGuide/Closures.html))
are used extensively in this project. The following source was used to learn
about lambda functions and how to implement them:
<https://www.w3schools.com/java/java_lambda.asp>.

### Singleton design pattern

Singleton design pattern used in the `Database` class based on this example:
<https://www.geeksforgeeks.org/singleton-class-java/>.

### Return auto-generated DB ID when inserting

The following sources were used to figure out how to get JDBC to return an
auto-generated id field when inserting a new record.

- <https://stackoverflow.com/a/56236430/11354266>
- <https://stackoverflow.com/a/1915197/11354266>

## Notes

- The main class (i.e. the execution entry-point) for the project is
  `cli.RecipeMgmt`.

## Setup

### Enter database login credentials

- Open the `database/Credentials.java` file.
- Replace placeholders with your username and password.
- Save the file.

## Building and running the application

- Open the project in NetBeans 8.2.
- Click the hammer icon on the top bar to "Build Project" (NOT "Run Project").
- On the left sidebar, click to highlight the `CSC545TermProject` project.
- Click the "Tools" system dropdown menu at the top of the screen and select
  "Open in Terminal".
- Type `java -jar dist/CSC545TermProject.jar` in the command line and hit enter.
- You should see a warning about missing a subcommand. This means the app is
  working as intended.

## Usage

### Food item management

```bash
java -jar dist/CSC545TermProject.jar food <subcommand>
```

Subcommands:

- `add`: Add a food item
- `delete`: Delete a food item
- `get`: Get the details of a food item
- `list`: List food items
- `update`: Update a food item's information

### Recipe management

```bash
java -jar dist/CSC545TermProject.jar recipe <subcommand>
```

Subcommands:

- `add`: Add a Recipe
- `delete`: Delete a recipe
- `get`: Get the details of a recipe
- `list`: List recipes
- `search`: Search for a recipe
  - You must provide one of the following options when running this subcommand:
    - `-c`: Category filter string
    - `-i`: Ingredient filter string
- `update`: Update a recipe's information

### Meal management

```bash
java -jar dist/CSC545TermProject.jar meals <subcommand>
```

Subcommands:

- `add`: Add a meal plan
- `delete`: Delete a meal plan
- `get`: Get the details of a meal plan
- `list`: List meal plans
- `update`: Update a meal plan's information

### Generate shopping list

```bash
java -jar dist/CSC545TermProject.jar shopping
```

## Open source software

This project is partially built with an open source library
[`picocli`](https://picocli.info) which made it easier to build the command line
interface. `picocli` provides useful error messages if the CLI input is
malformed and the `--help` option lists what subcommands in the program can be
run.
