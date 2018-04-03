public enum Command {
    //команды
    REG("/reg"), AUTH("/auth"), AUTHOK("/authok"), AUTHERROR("/autherror"), LS("/ls"), UPLOAD("/upload"), DOWNLOAD("/upload"), DELETE("/del"), NONE("/none");

    private String commandString;

    Command(String commandString) {
        this.commandString = commandString;
    }

    public String getCommandString() {
        return commandString;
    }

    public static Command getCommand(String commandCode) {
        switch (commandCode) {
            case "/reg": return REG; //запрос на регистрацию
            case "/auth": return AUTH; //запрос на авторизацию
            case "/authok": return AUTHOK; //авторизация успешна
            case "/autherror": return AUTHERROR; //ошибки при авторизации
            case "/ls": return LS;
            default:
                return NONE; //команда не распознана
        }
    }

}
