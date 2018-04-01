public enum Commands {
    //команды
    REG("/reg"), AUTH("/auth"), LS("/ls"), UPLOAD("/upload"), DOWNLOAD("download"), DELETE("/del");

    private String commandString; //строковое значение команд
    Commands(String commandString) {
        this.commandString = commandString;
    }

    public String getCommandString() {
        return commandString;
    }
}
