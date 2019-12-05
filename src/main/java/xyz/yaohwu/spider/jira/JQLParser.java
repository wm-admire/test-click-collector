package xyz.yaohwu.spider.jira;

/**
 * @author yaohwu
 */
public enum JQLParser {

    /**
     * username
     */
    USERNAME {
        @Override
        public String parser(String template, String formulaResult) {
            return template.replaceAll(getPattern(), "\"" + formulaResult + "\"");
        }

        @Override
        public String getPattern() {
            return "currentUser\\(\\)";
        }
    };


    public abstract String parser(String template, String formulaResult);

    public abstract String getPattern();
}
