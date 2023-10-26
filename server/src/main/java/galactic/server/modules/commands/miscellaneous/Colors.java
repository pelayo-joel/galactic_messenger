package galactic.server.modules.commands.miscellaneous;

public enum Colors {
    DEFAULT("\033[0m"),
    RED("\033[0;31m"),
    GREEN("\033[0;32m"),
    YELLOW("\033[0;33m"),
    BLUE("\033[0;34m"),
    PURPLE("\033[0;35m"),
    CYAN_UNDERLINED("\033[4;36m"),
    WHITE("\033[0;37m");


    private String code;



    private Colors(String colorCode) {
        this.code = colorCode;
    }



    @Override
    public String toString() { return this.code; }
}
