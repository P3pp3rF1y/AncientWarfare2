package net.shadowmage.ancientwarfare.structure.api;

@SuppressWarnings("serial")
public class TemplateParsingException extends Exception {

    public TemplateParsingException() {
    }

    public TemplateParsingException(String arg0) {
        super(arg0);
    }

    public TemplateParsingException(Throwable arg0) {
        super(arg0);
    }

    public TemplateParsingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public static class TemplateRuleParsingException extends TemplateParsingException {

        public TemplateRuleParsingException(String arg0) {
            super(arg0);
        }

        public TemplateRuleParsingException(Throwable arg0) {
            super(arg0);
        }

        public TemplateRuleParsingException(String arg0, Throwable arg1) {
            super(arg0, arg1);
        }

        @Override
        public String getLocalizedMessage() {
            return super.getLocalizedMessage();
        }

    }


}
