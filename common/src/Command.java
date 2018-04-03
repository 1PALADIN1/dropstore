public enum Command {
    //команды
    REG("/reg"), AUTH("/auth"), AUTHOK("/authok"), LS("/ls"), UPLOAD("/upload"), DOWNLOAD("/upload"), DELETE("/del"), NONE("/none");

    private String commandString;

    Command(String commandString) {
        this.commandString = commandString;
    }

    public String getCommandString() {
        return commandString;
    }

    public static Command getCommand(String commandCode) {
        switch (commandCode) {
            case "/reg": return REG;
            case "/auth": return AUTH;
            case "/authok": return AUTHOK;
            case "/ls": return LS;
            default:
                return NONE; //команда не распознана
        }
    }

}
