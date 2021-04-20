package cli;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Exception thrown when there is an error parsing an inputted value.
 */
class ArgumentParsingException extends Exception {

    public ArgumentParsingException(String errorMessage) {
        super(errorMessage);
    }
}

/**
 * Lambda function for validating an input.
 *
 * Lambda takes a generic value and returns an error message if there's an error, else
 * Optional.empty().
 */
interface ValidateInputLambda<T> {
    Optional<String> run(T input);
}

/**
 * Lambda function that converts a string input to some type, throwing if there's an error.
 */
interface CasterLambda<T> {
    T run(String input) throws ArgumentParsingException;
}

/**
 * Lambda function which reads string data from a scanner and returns it.
 */
interface ScannerReaderLambda {
    String run(Scanner input);
}

/**
 * Lambda function which provides an active input scanner and can throws SQL errors.
 *
 * For user interaction in the CLI.
 */
interface UserDbInteractionLambda {
    Integer run(Scanner input) throws SQLException;
}

/**
 * Contains lambdas for input validation.
 *
 * Note: This was designed to work better than it actually does... Unfortunately in Java, generic
 * arrays cannot be initialized without casting them and ignoring unchecked errors. This class of
 * validators works, but it not as clean as the original design.
 */
class InputValidators {

    /**
     * Validate the max length of an input string.
     *
     * @param length max length to check
     * @return error message if there's an issue, else `Optional.empty()`
     */
    @SuppressWarnings("unchecked")
    protected static ValidateInputLambda<String>[] maxLengthValidator(int length) {
        return new ValidateInputLambda[] {
            value -> {
                String valueString = (String) value;
                if (valueString.length() > length) {
                    return Optional.of(
                        String.format("Value can only be %s characters long", length)
                    );
                }
                return Optional.empty();
            },
        };
    }

    /**
     * Validate that an integer is positive.
     *
     * @return error message if there's an issue, else `Optional.empty()`
     */
    @SuppressWarnings("unchecked")
    protected static ValidateInputLambda<Integer>[] positiveIntegerValidator() {
        return new ValidateInputLambda[] {
            value -> {
                Integer valueInt = (Integer) value;
                if (valueInt < 0) {
                    return Optional.of("Integer must be positive");
                }
                return Optional.empty();
            },
        };
    }
}

/**
 * Model CLI superclass for managing a database model.
 */
public abstract class ModelCli {

    /**
     * For repeatedly prompting, parsing, and validating an input value from a user.
     *
     * @param <T> type of parsed value
     * @param prompt to ask user for input
     * @param validators validation functions to run on the parsed input
     * @param caster function to parse raw input to requested type
     * @param required whether this input can be left blank (user just hits enter)
     * @param scanner Scanner instance to read input
     * @return validated input if valid one given, else Optional.empty()
     */
    protected <T> Optional<T> validatedInput(
        String prompt,
        ValidateInputLambda<T>[] validators,
        CasterLambda<T> caster,
        boolean required,
        Scanner scanner,
        ScannerReaderLambda reader
    ) {
        // Keep trying until valid input given
        while (true) {
            // Prompt and get value
            System.out.print(prompt);
            var value = reader.run(scanner);
            // Remove whitespace
            value = value.strip();
            if (value.isBlank()) {
                // If blank and required, try again
                if (required) {
                    System.out.println("Value is required");
                    continue;
                }
                // Else, just accept it
                return Optional.empty();
            }
            // Try to parse (cast) the raw input
            T castedValue;
            try {
                castedValue = caster.run(value);
            } catch (ArgumentParsingException e) {
                System.out.println("Incorrect type");
                continue;
            }
            // If there are validators, run them
            if (validators != null) {
                boolean isError = false;
                for (var validator : validators) {
                    var output = validator.run(castedValue);
                    // If validator gave output, print the error and indicate there was an error
                    if (output.isPresent()) {
                        System.out.println(output.get());
                        isError = true;
                    }
                }
                // If there was an error, try again
                if (isError) {
                    continue;
                }
            }
            return Optional.of(castedValue);
        }
    }

    /**
     * Specialized validated input for reading strings.
     *
     * @param prompt to ask user for input
     * @param validators validation functions to run on the parsed input
     * @param required whether this input can be left blank (user just hits enter)
     * @param scanner Scanner instance to read input
     * @return validated string input if valid one given, else Optional.empty()
     */
    protected Optional<String> validatedString(
        String prompt,
        ValidateInputLambda<String>[] validators,
        boolean required,
        Scanner scanner
    ) {
        return validatedInput(
            prompt,
            validators,
            value -> {
                return value;
            },
            required,
            scanner,
            readerScanner -> {
                return readerScanner.nextLine();
            }
        );
    }

    /**
     * Validate string input with simple length specification.
     */
    protected Optional<String> validatedString(
        String prompt,
        Integer maxLength,
        boolean required,
        Scanner scanner
    ) {
        return validatedString(
            prompt,
            InputValidators.maxLengthValidator(maxLength),
            required,
            scanner
        );
    }

    /**
     * Validate string with no extra validation functions.
     */
    protected Optional<String> validatedString(String prompt, boolean required, Scanner scanner) {
        return validatedInput(
            prompt,
            null,
            value -> {
                return value;
            },
            required,
            scanner,
            readerScanner -> {
                return readerScanner.nextLine();
            }
        );
    }

    /**
     * Specialized validated input for reading multiline strings.
     *
     * @param prompt to ask user for input
     * @param required whether this input can be left blank
     * @param scanner Scanner instance to read input
     * @return validated string input if valid one given, else Optional.empty()
     */
    protected Optional<String> validatedMultilineString(
        String prompt,
        boolean required,
        Scanner scanner
    ) {
        return validatedInput(
            prompt,
            null,
            value -> {
                return value;
            },
            required,
            scanner,
            readerScanner -> {
                String lines = "";
                // Read lines until a single line with "/" is entered
                while (true) {
                    var line = readerScanner.nextLine();
                    if (line.equals("/")) {
                        return lines;
                    } else {
                        lines += line + "\n";
                    }
                }
            }
        );
    }

    /**
     * Specialized validated input for reading integers.
     *
     * @param prompt to ask user for input
     * @param validators validation functions to run on the parsed input
     * @param required whether this input can be left blank (user just hits enter)
     * @param scanner Scanner instance to read input
     * @return validated int input if valid one given, else Optional.empty()
     */
    protected Optional<Integer> validatedInt(
        String prompt,
        ValidateInputLambda<Integer>[] validators,
        boolean required,
        Scanner scanner
    ) {
        return validatedInput(
            prompt,
            validators,
            value -> {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new ArgumentParsingException("Not an integer");
                }
            },
            required,
            scanner,
            readerScanner -> {
                return readerScanner.nextLine();
            }
        );
    }

    /**
     * Specialized validated input for reading positive integers.
     *
     * @param prompt to ask user for input
     * @param required whether this input can be left blank (user just hits enter)
     * @param scanner Scanner instance to read input
     * @return validated int input if valid one given, else Optional.empty()
     */
    protected Optional<Integer> validatedPositiveInt(
        String prompt,
        boolean required,
        Scanner scanner
    ) {
        return validatedInput(
            prompt,
            InputValidators.positiveIntegerValidator(),
            value -> {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new ArgumentParsingException("Not an integer");
                }
            },
            required,
            scanner,
            readerScanner -> {
                return readerScanner.nextLine();
            }
        );
    }

    /**
     * For handling user interaction with System.in and the DB.
     *
     * @param interactionHandler lambda which handles user interaction
     */
    protected Integer userInteraction(UserDbInteractionLambda interactionHandler) {
        try (var scanner = new Scanner(System.in)) {
            return interactionHandler.run(scanner);
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * Abstract method to handle adding a model entry to the DB.
     *
     * @return exit code
     */
    abstract int add();

    /**
     * Abstract method to handle listing a model's entries from the DB.
     *
     * @return exit code
     */
    abstract int list();

    /**
     * Abstract method to handle getting a model entry from the DB.
     *
     * @return exit code
     */
    abstract int get();

    /**
     * Abstract method to handle updating and saving a model entry from the DB.
     *
     * @return exit code
     */
    abstract int update();

    /**
     * Abstract method to handle deleting a model entry from the DB.
     *
     * @return exit code
     */
    abstract int delete();
}
