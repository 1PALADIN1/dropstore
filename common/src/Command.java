public enum Command {
    //команды
    REG("/reg"), AUTH("/auth"), OK("/ok"), ERROR("/error"), LS("/ls"), UPLOAD("/upload"), DOWNLOAD("/download"), DELETE("/del"), CONTINUE("/continue"), CREATEDIR("/createdir"), NONE("/none");

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
            case "/ok": return OK; //успешное выполнение команды
            case "/error": return ERROR; //ошибка при выполнении операции
            case "/ls": return LS; //список файлов
            case "/continue": return CONTINUE; //продолжить выполнение (например, сервер отсылает эту команду, когда готов принимать файлы)
            case "/upload": return UPLOAD; //загрузить на сервер
            case "/download": return DOWNLOAD; //скачать с сервера
            case "/del": return DELETE; //удалить с сервера
            case "/createdir": return CREATEDIR; //содание директории на сервере
            default:
                return NONE; //команда не распознана
        }
    }

}
