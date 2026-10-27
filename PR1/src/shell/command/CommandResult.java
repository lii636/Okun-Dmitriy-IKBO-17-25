package shell.command;

public record CommandResult(String output, boolean exits) {
    public static CommandResult continueExecution() {
        return show("");
    }

    public static CommandResult show(String output) {
        return new CommandResult(output, false);
    }

    public static CommandResult exit() {
        return new CommandResult("", true);
    }
}
